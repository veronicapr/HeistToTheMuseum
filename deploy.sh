#Addresses
Repository="l040101-ws01.ua.pt";
Museum="l040101-ws02.ua.pt";
ConcentrationSite="l040101-ws03.ua.pt";
ControlSite="l040101-ws04.ua.pt";
AssaultParty="l040101-ws05.ua.pt";
Thief="l040101-ws06.ua.pt";
MasterThief="l040101-ws07.ua.pt";
#Port
Port="22307";
#Other
Path="~/Heist-RMI/";
Options="-classpath $Path -Djava.rmi.server.codebase=file:$Path -Djava.security.policy=security.policy";
Log="Heist_Log.txt";


for i in 01 02 03 04 05 06 07
do
	echo "sd0307@l040101-ws$i.ua.pt";
	sshpass -f password scp "Heist-RMI.zip" sd0307@l040101-ws$i.ua.pt:~;
	sshpass -f password ssh sd0307@l040101-ws$i.ua.pt 'unzip -o "Heist-RMI.zip"';
done

#xterm -hold -e "echo 'RMI Registry'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 22307 &'" &
#sleep 2;
xterm -hold -e "echo 'General Repository'; sshpass -f password ssh sd0307@$Repository 'cd Heist-RMI/; java $Options heist/repository/GeneralRepository $Port $Log &'" &
sleep 2;
xterm -hold -e "echo 'Museum'; sshpass -f password ssh sd0307@$Museum 'cd Heist-RMI/; java $Options heist/museum/Museum $Repository $Port &'" &
sleep 2;
xterm -hold -e "echo 'Concentration Site'; sshpass -f password ssh sd0307@$ConcentrationSite 'cd Heist-RMI/; java $Options heist/concentration_site/OrdinaryThievesConcentrationSite $Repository $Port &'" &
sleep 2;
xterm -hold -e "echo 'Control Site'; sshpass -f password ssh sd0307@$ControlSite 'cd Heist-RMI/; java $Options heist/control_site/MasterThiefControlCollectionSite $Repository $Port &'" &
sleep 2;
xterm -hold -e "echo 'Assault Party'; sshpass -f password ssh sd0307@$AssaultParty 'cd Heist-RMI/; java $Options heist/assault_party/AssaultParty $Repository $Port &'" &
sleep 2;
xterm -hold -e "echo 'Ordinary Thieves'; sshpass -f password ssh sd0307@$Thief 'cd Heist-RMI/; java $Options heist/thieves/Thief  $Museum $AssaultParty $ControlSite $ConcentrationSite $Port &'" &
sleep 2;
xterm -hold -e "echo 'Master Thief'; sshpass -f password ssh sd0307@$MasterThief 'cd Heist-RMI/; java $Options heist/thieves/MasterThief $Repository $Museum $AssaultParty $ControlSite $ConcentrationSite $Port &'" &
sleep 30;

echo "copying $Log file";
sshpass -f password scp sd0307@$Repository:$Path$Log .;
