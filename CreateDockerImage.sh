#!/bin/sh
# Dette shellscriptet lager et dockerimage av springsec-oidc-backend tjenesten
# 
Doit(){
	MakeRepo
	MakeUnixStartupScript 
	# # MakeDosStartupBatfile # Denne kan vaere nyttig for window$, please keep...
	MakeCompressedDockerContext
	DockerBuild
}
MakeRepo(){
	if [ -d repo ];then 
		mkdir -p repo-old
		mv repo repo-old/repo-$(date '+%Y%m%d-%H%M'); 
	fi
	mvn -DoutputDirectory=repo -DexcludeArtifactIds=junit dependency:copy-dependencies # Inkluderer testdependencies
	mvn clean install
	cp target/$(FindArtifact) repo
	KopierSrcTestResources # Quick and dirty kopiere ressursfiler...

}



DockerBuild(){
	# Vi avhenger av dockerimage ibmjava:sfj-alpine og maa avbryte hvis det mangler fordi vi ikke har
	# et tilgjengelig docker-registry p.t.
	# local basisdockerimagename="ibmjava:sfj-alpine" # NB: Det kan vaere aktuelt aa endre dette imaget til noe annet
	local basisdockerimagename="openjdk:9-jre-slim" # NB: Det kan vaere aktuelt aa endre dette imaget til noe annet
	if [ "$(docker images $basisdockerimagename --format {{.Repository}}":"{{.Tag}})" != "$basisdockerimagename" ];then
		echo Dockerimage $basisdockerimagename Finnes ikke, vi kan ikke fortsette, avslutter 1>&2
		echo Se til at du laster dette imaget med en kommando a-la... 1>&2
		echo docker load -i ./$basisdockerimagename.dockerimage 1>&2
		exit 1
	fi

	local Version=$(xmllint --xpath "string(//*[local-name()='version'])" pom.xml);
	local ArifactId=$(GetHierarcicalArtifactId)
        local TgzFileName=${ArifactId}-latest-dockercontext.tgz

	docker build --tag $ArifactId:$Version - < $TgzFileName
	docker tag $ArifactId:$Version $ArifactId:latest
	docker images $ArifactId

	ToDir=/z/docker-kjoreseddel-springsec-oidc/
	docker save -o ${ToDir}${ArifactId}-latest.dockerimage $ArifactId:latest
	ls -ltr  ${ToDir}${ArifactId}-latest.dockerimage
}

GetHierarcicalArtifactId(){
	echo 'setns ns=http://maven.apache.org/POM/4.0.0
	xpath string(/ns:project/ns:artifactId)'|\
	xmllint --shell pom.xml|awk '/Object/{printf "%s\n",$(NF);}'
}

KopierSrcTestResources(){
	if [ ! -d conf ];then mkdir conf; fi

	cp src/test/resources/logback-test.xml conf/standalone_logback.xml
	# cp src/test/resources/applikasjon-environment.properties conf/
	# cp src/test/resources/buildnumber.properties conf/

	# # Disse trengs av SslHelperen
	#cp src/test/resources/keystore.jks conf/
	#cp src/test/resources/datapower_truststore.jks conf/
}

GetPackaging(){
	local Packaging=$(xmllint --xpath "string(//*[local-name()='packaging'])" pom.xml);
	if [ "$Packaging" = "" ];then echo jar;else echo $Packaging; fi;
}
FindArtifact(){
	local packType="$(GetPackaging)";
	if [ "$packType" = "jar" ];then FindJarArtifact; elif [ "$packType" = "war" ];then FindWarArtifact; fi
}
FindJarArtifact(){
	local Version=$(xmllint --xpath "string(//*[local-name()='version'])" pom.xml);
	local ArifactId=$(GetHierarcicalArtifactId)
	local FileName=$ArifactId-$Version.jar
	if [ -f target/$FileName ];then 
		echo $FileName; 
	else echo Feil, jar-artifactId $Filename finnes ikke; exit; fi
}
FindWarArtifact(){
	local Version=$(xmllint --xpath "string(//*[local-name()='version'])" pom.xml);
	local ArifactId=$(GetHierarcicalArtifactId)
	local FileName=$ArifactId-$Version/WEB-INF/lib/$ArifactId-$Version.jar
	if [ -f target/$FileName ];then 
		echo $FileName; 
	else echo Feil, jar-war-artifactId $Filename finnes ikke; exit; fi
}

MakeUnixStartupScript(){
	CodeForWaitUntil(){
        cat <<-! | tr '~' '$'
		WaitUntilHostPortIsAvailable(){
		if [ "~1" = "" ];then return 0; fi
		arg(){ echo ~1|awk -F: -v n=~2 '{printf "%s\n",~(n);}'; }
		local addr=~(arg ~1 1);
		local port=~(arg ~1 2);
		echo Waiting for ~addr:~port to become available
		while ! timeout 1 bash -c \\
		"cat < /dev/null > /dev/tcp/~addr/~port" \\
		 >/dev/null 2>&1; do sleep 0.3; done
		echo ~addr:~port is up
		}
		WaitUntilHostPortIsAvailable ~1
	!
	}
	# local MainClass=no.politiet.fellestjenester.folkeregister.dockerjettystarter.JettyFregStarter
	# local MainClass=no.politiet.datapowermock.JettyStarter
	local MainClass=no.politiet.ft.HttpJettyRunner
	local F=run.sh
	rm $F
	echo '#!/bin/sh' > $F
	# CodeForWaitUntil >> $F
	for x in repo/*jar; do echo t=\${t}$x: >> $F; done
	echo t=\${t}conf >> $F
	echo 'exec java -Dlogback.configurationFile=conf/standalone_logback.xml -cp $t '$MainClass >> $F
	cat $F
	ls -l $F
}

# Please behold MakeDosStartupBatfile()-metoden, vi kan trenge den senere...
# Brukes ikke ifbm Docker, men kan vaere nyttig for testing paa window$
MakeDosStartupBatfile(){ 
	F=run.bat
	rm $F
	echo @echo off > $F
	echo set t= >> $F
	for x in repo/*jar; do echo set t=%t%$x\; >> $F; done
	echo java -Dfile.encoding=utf8 -cp %t% no.politiet.fellestjenester.folkeregister.dockerjettystarter.JettyFregStarter %1 %2 %3 %4 %5 %6 %7 %8 %9 >> $F
	unix2dos $F
}

MakeCompressedDockerContext(){
	local ArifactId=$(GetHierarcicalArtifactId)
	local TgzFileName=${ArifactId}-$(date '+%Y%m%d-%H%M').tgz
	for x in $(ls ${ArifactId}-*tgz);do
		echo Fjerner gammel versjon $x
		rm $x
	done
	MakeDockerfile
	# src/main/webapp/index.html 
	tar cvf - Dockerfile run.sh conf repo src/main/webapp | gzip > $TgzFileName
	rm ./Dockerfile
	echo Laget ny tar-file $TgzFileName 
	echo Linker $TgzFileName til ${ArifactId}-latest-dockercontext.tgz for aa kunne referere til et fast filnavn
	ln -s $TgzFileName ${ArifactId}-latest-dockercontext.tgz
}

MakeDockerfile(){
	# FROM ibmjava:sfj-alpine
	cat <<-! > Dockerfile
		FROM openjdk:9-jre-slim
		ADD conf /conf
		ADD repo /repo
		ADD src  /src
		ADD run.sh /
		# VOLUME ["/conf", "/conf"]
		EXPOSE 8081
		CMD sh /run.sh 
	!
}

Doit
