#!/bin/bash  

declare -a arr=("Dos" "EmailSending" "ExploitSending" "Scanning" "EvasionEvent" "AntiAnalysis" "AntiDefence" "DebuggerChecking")         
arr+=("EnvironmentDetection" "RemovalOfEvidence" "RemovalOfRegistries" "ShutdownOfDefenceMechanisms" "AttackLaunchingEvent")
arr+=("RemoteControlEvent" "DownloadCode" "GetCommand" "KnownMalwareExecution" "AttackLaunchingEvent" "OtherCodeExecution")
arr+=("BinaryFile" "ConfigurationFile" "IRC_IM_Connection" "SelfDefenceEvent" "Maintenance" "ComponentChecking")
arr+=("CreateSynchronizationObject" "LanguageChecking" "Persistence" "StealingEvent" "SystemInformationStealing" "UserInformationStealing")
arr+=("Hostname" "OSInformation" "ResourceInformation" "Credential" "InternetBankingData" "SubVersionEvent" "Browser")
arr+=("MemoryWriting" "OperatingSystem")

echo " "
echo "===== Start generator script ====="  

for NAME in ${arr[@]} 
do
	echo "- $NAME"
	rm ${NAME}Agent.owl
	cp agents.owl ${NAME}Agent.owl
done

echo "===== Done ====="
echo " "
