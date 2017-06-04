

for i in 01 02 03 04 05 06 08 09
do
	echo "sd0307@l040101-ws$i.ua.pt";
	sshpass -f password scp "Heist-RMI.zip" sd0307@l040101-ws$i.ua.pt:~;
	sshpass -f password ssh sd0307@l040101-ws$i.ua.pt 'unzip -o "Heist-RMI.zip"';
done

xterm -hold -e "rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false 22307 &"
sleep 1;
xterm -hold -e "echo 'General Repository'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/repository/GeneralRepository 22307'" &
sleep 1;
xterm -hold -e "echo 'Museum'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/museum/Museum 22307'" &
sleep 1;
xterm -hold -e "echo 'Concentration Site'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/concentration_site/OrdinaryThievesConcentrationSite 22307'" &
sleep 1;
xterm -hold -e "echo 'Control Site'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/control_site/MasterThiefControlCollectionSite 22307'" &
sleep 1;
xterm -hold -e "echo 'Assault Party'; sshpass -f password ssh sd0307@l040101-ws01.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/assault_party/AssaultParty 22307'" &
sleep 1;
xterm -hold -e "echo 'Ordinary Thieves'; sshpass -f password ssh sd0307@l040101-ws03.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/thieves/Thief l040101-ws01.ua.pt'" &
sleep 1;
xterm -hold -e "echo 'Master Thief'; sshpass -f password ssh sd0307@l040101-ws04.ua.pt 'cd Heist-RMI/src/; java -cp Heist-RMI.jar -Djava.rmi.server.codebase=file:${workspace_loc} -Djava.security.policy=security.policy heist/thieves/MasterThief 22307 l040101-ws01.ua.pt'" &
sleep 20;

echo "copying report.log file";
sshpass -f password scpsd0307@l040101-ws05.ua.pt:~/src/report.log .;
