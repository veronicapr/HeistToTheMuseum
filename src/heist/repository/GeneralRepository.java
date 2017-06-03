/*
 * General Repository
 */
package heist.repository;

import settings.HeistSettings;
import genclass.GenericIO;
import genclass.TextFile;
import heist.enums.State_MasterThief;
import heist.enums.State_Thief;
import heist.repository.interfaces.It_Repository_Museum;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * <p>
 * Control for all log associated matters.</p>
 * <p>
 * Requires posterior association with Museum, Master Thief, Master Thief Control Collection Site, Assault Parties, Thieves in order to work.</p>
 *
 * @author Verónica Rocha nmec 68809
 * @author Miguel Ferreira nmec 72583
 */
public class GeneralRepository extends UnicastRemoteObject implements It_Repository_Museum {

	//========================================================================================================================//
	// Museum Data
	//========================================================================================================================//
	/**
	 * Array containing the number of paintings in each room
	 */
	private final int[] rooms_paintings = new int[HeistSettings.TOTAL_ROOMS];
	/**
	 * Array containing the distance of each room in the museum
	 */
	private final int[] rooms_distance = new int[HeistSettings.TOTAL_ROOMS];
	//========================================================================================================================//
	// Assault Parties
	//========================================================================================================================//
	/**
	 * Team members positions
	 */
	private final int[][] team_positions = new int[HeistSettings.TOTAL_TEAMS][HeistSettings.TEAM_SIZE];
	//========================================================================================================================//
	// Thief
	//========================================================================================================================//
	/**
	 * Thieves assault party id
	 */
	private final int[] thieves_assault_party_id = new int[HeistSettings.TOTAL_THIEVES];
	/**
	 * Thieves agility
	 */
	private final int[] thieves_agility = new int[HeistSettings.TOTAL_THIEVES];
	/**
	 * Thieves pockets
	 */
	private final int[] thieves_canvas = new int[HeistSettings.TOTAL_THIEVES];
	/**
	 * Thieves states
	 */
	private final State_Thief[] thieves_states = new State_Thief[HeistSettings.TOTAL_THIEVES];
	//========================================================================================================================//
	// Master Thief
	//========================================================================================================================//
	/**
	 * Master thief saved state
	 */
	private State_MasterThief master_thief_state;

	//========================================================================================================================//
	// General Repository Contructor
	//========================================================================================================================//
	/**
	 * Constructor for GeneralRepository, also creates the associated log file with its title and header.
	 */
	private GeneralRepository() throws RemoteException {
		super();
		// initialise thief info
		for (int thief_index = 0; thief_index < HeistSettings.TOTAL_THIEVES; thief_index++) {
			thieves_agility[thief_index] = 0;
			thieves_canvas[thief_index] = 0;
			thieves_assault_party_id[thief_index] = -1;
		}
		// start log
		logStart();
	}

	//========================================================================================================================//
	// General repository server info and main
	//========================================================================================================================//
	/**
	 * General repository object reference [singleton]
	 */
	private static GeneralRepository self;
	/**
	 * Server port number
	 */
	private static int repository_port_number;
	/**
	 * Registry port number
	 */
	private static int registry_port_number;
	/**
	 * Log file name
	 */
	private static String log_name;
	/**
	 * File manipulation class
	 */
	private final TextFile log = new TextFile();

	/**
	 * General repository server start, requires 4 argument.
	 *
	 * @param args program arguments should be:
	 * <ul>
	 * <li>registry port number</li>
	 * <li>general repository log name</li>
	 * <li>server port number</li>
	 * </ul>
	 */
	public static void main(String[] args) {
		if (args.length != 4) {
			GenericIO.writelnString("Wrong number of arguments!");
			System.exit(1);
		} else {
			try {
				registry_port_number = Integer.parseInt(args[0]);
				log_name = args[1];
				repository_port_number = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				GenericIO.writelnString("Port number must be an integer!");
				System.exit(1);
			}
		}
		// security manager
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		GenericIO.writelnString("Security manager was installed!");
		
		//TODO - Add latest code
	}

	//========================================================================================================================//
	// Log updates - Museum interface
	//========================================================================================================================//
	/**
	 * Log line containing full updated museum info
	 *
	 * @param rooms_paintings rooms current paintings full info
	 * @param rooms_distance rooms distance full info
	 */
	@Override
	public synchronized final void logLine_MuseumUpdateFull(int[] rooms_paintings, int[] rooms_distance) throws RemoteException {
		for (int room_index = 0; room_index < HeistSettings.TOTAL_ROOMS; room_index++) {
			this.rooms_paintings[room_index] = rooms_paintings[room_index];
			this.rooms_distance[room_index] = rooms_distance[room_index];
		}
		logLine();
	}

	/**
	 * Log line containing updated museum info over a single room
	 *
	 * @param room_index to be updated room index
	 * @param room_paintings current number of paintings in the room
	 */
	@Override
	public synchronized final void logLine_MuseumUpdateSingle(int room_index, int room_paintings) throws RemoteException {
		this.rooms_paintings[room_index] = room_paintings;
		logLine();
	}

	//========================================================================================================================//
	// Logging methods
	//========================================================================================================================//
	/**
	 * Creates log and prints title and header.
	 */
	public synchronized final void logStart() {
		log.openForWriting(null, log_name);
		log.writeString(titleGenerator(), headerGenerator());
		log.close();
	}

	/**
	 * Ends the log by adding a fixed sentence and the legend.
	 */
	public synchronized final void logFinish() {
		this.master_thief_state = State_MasterThief.PRESENTING_THE_REPORT;
		for (int thief_index = 0; thief_index < HeistSettings.TEAM_SIZE; thief_index++) {
			thieves_states[thief_index] = State_Thief.HEAR_REPORT;
		}

		String end = String.format("My friends, tonight's effort produced %1$2d priceless paintings!\n", 0);

		log.openForAppending(null, log_name);
		log.writeString(end, legendGenerator());
		log.close();
	}

	/**
	 * Generates the log's title.
	 *
	 * @return string containing log's title
	 */
	private String titleGenerator() {
		return "Heist to the Museum - Description of the internal state\n\n";
	}

	/**
	 * Generates log's header with fixed information.
	 *
	 * @return string containing the first 5 lines
	 */
	private String headerGenerator() {
		String line_1 = "MstT";
		String line_2 = "Stat";
		String line_3 = "    ";
		String line_4 = "    ";
		String line_5 = "    ";
		for (int team_index = 0; team_index < HeistSettings.TOTAL_TEAMS; team_index++) {
			line_1 += "    ";
			line_2 += "    ";
			line_3 += String.format("       Assault party %1$02d       ", team_index + 1);
			line_4 += "    ";
			line_5 += "RId ";
			for (int thief_index = team_index * HeistSettings.TEAM_SIZE; thief_index < (team_index + 1) * HeistSettings.TEAM_SIZE; thief_index++) {
				if (HeistSettings.TEAM_SIZE == 1) {
					line_1 += "   Thief 01           ";
					line_2 += "Stat S MD             ";
					line_3 += "";
					line_4 += "   Elem 01            ";
					line_5 += "Id Pos Cv             ";
				}
				if ((HeistSettings.TEAM_SIZE > 2) && (thief_index % HeistSettings.TEAM_SIZE > 1)) {
					line_1 += String.format("   Thief %1$02d  ", thief_index + 1);
					line_2 += "Stat S MD    ";
					line_3 += String.format("%1$13s", "             ");
					line_4 += String.format("   Elem %1$02d   ", (thief_index % HeistSettings.TEAM_SIZE) + 1);
					line_5 += "  Id Pos Cv  ";
				} else {
					line_1 += String.format("   Thief %1$02d  ", thief_index + 1);
					line_2 += "Stat S MD    ";
					line_3 += "";
					line_4 += String.format("   Elem %1$02d   ", (thief_index % HeistSettings.TEAM_SIZE) + 1);
					line_5 += "  Id Pos Cv  ";
				}
			}
		}

		line_3 += "Museum  ";
		for (int room_index = 0; room_index < HeistSettings.TOTAL_ROOMS; room_index++) {
			line_4 += String.format("Room %1$02d ", room_index + 1);
			line_5 += "NP DT   ";
		}
		return line_1 + "\n" + line_2 + "\n" + line_3 + "\n" + line_4 + "\n" + line_5 + "\n";
	}

	/**
	 * Generates the log legend.
	 *
	 * @return string containing log's legend
	 */
	private String legendGenerator() {
		String legend = "Legend:\nMstT Stat    – state of the master thief\n";
		legend += String.format("Thief # Stat - state of the ordinary thief # (# - 1 .. %d)\n",
				HeistSettings.TOTAL_THIEVES);
		legend += String.format("Thief # S    – situation of the ordinary thief # (# - 1 .. %d) either 'W' (waiting to join a party) or 'P' (in party)\n",
				HeistSettings.TOTAL_THIEVES);
		legend += String.format("Thief # MD   – maximum displacement of the ordinary thief # (# - 1 .. %d) a random number between %d and %d\n",
				HeistSettings.TOTAL_THIEVES, HeistSettings.MIN_AGILITY, HeistSettings.MAX_AGILITY);
		legend += String.format("Assault party # RId        – assault party # (# - 1,%d) elem # (# - 0 .. %d) room identification (1 .. %d)\n",
				HeistSettings.TOTAL_TEAMS, HeistSettings.TEAM_SIZE - 1, HeistSettings.TOTAL_ROOMS);
		legend += String.format("Assault party # Elem # Id  – assault party # (# - 1,%d) elem # (# - 0 .. %d) member identification (1 .. %d)\n",
				HeistSettings.TOTAL_TEAMS, HeistSettings.TEAM_SIZE - 1, HeistSettings.TOTAL_THIEVES);
		legend += String.format("Assault party # Elem # Pos – assault party # (# - 1,%d) elem # (# - 0 .. %d) present position (0 .. DT RId)\n",
				HeistSettings.TOTAL_TEAMS, HeistSettings.TEAM_SIZE - 1);
		legend += String.format("Assault party # Elem # Cv  – assault party # (# - 1,%d) elem # (# - 0 .. %d) carrying a canvas (0,1)\n",
				HeistSettings.TOTAL_TEAMS, HeistSettings.TEAM_SIZE - 1);
		legend += String.format("Museum Room # NP - room identification (1 .. %d) number of paintings presently hanging on the walls\n",
				HeistSettings.TOTAL_ROOMS);
		legend += String.format("Museum Room # DT - room identification (1 .. %d) distance from outside gathering site, a random number between %d and %d\n\n",
				HeistSettings.TOTAL_ROOMS, HeistSettings.MIN_DISTANCE, HeistSettings.MAX_DISTANCE);
		return legend;
	}

	/*
	 * Log line printed every time an update occurs.
	 */
	public synchronized final void logLine() {
		String line_1 = "";
		String line_2 = "";

		line_1 += String.format("%1$4s", getMasterThiefStateNum(master_thief_state));
		line_2 += "    ";

		for (int team_index = 0; team_index < HeistSettings.TOTAL_TEAMS; team_index++) {
			line_1 += "    ";
			line_2 += String.format("%1$2d  ", team_index);

			for (int thief_index = team_index * HeistSettings.TEAM_SIZE; thief_index < (team_index + 1) * HeistSettings.TEAM_SIZE; thief_index++) {
				line_1 += String.format("%1$4s %2$1s %3$2d    ",
						getThiefStateNum(thieves_states[thief_index]), thieves_assault_party_id[thief_index] > 0 ? "W" : "P", thieves_agility[thief_index]);
				line_2 += String.format("  %1$2d %2$3s %3$2d  ",
						thief_index, team_positions[team_index][thief_index % HeistSettings.TEAM_SIZE], thieves_canvas[thief_index]);
			}
		}

		for (int room_index = 0; room_index < HeistSettings.TOTAL_ROOMS; room_index++) {
			line_2 += String.format("%1$2d %2$3d  ", rooms_paintings[room_index], rooms_distance[room_index]);
		}

		log.openForAppending(null, log_name);
		log.writeString(line_1, "\n", line_2, "\n\n");
		log.close();
	}

	//========================================================================================================================//
	// Auxiliar Methods
	//========================================================================================================================//
	/**
	 * Converts the state of the Master thief a number.
	 *
	 * @param state master thief state
	 * @return state number reference or null
	 */
	public String getMasterThiefStateNum(State_MasterThief state) {
		switch (state) {
			case PLANNING_THE_HEIST:
				return "1000";
			case DECIDING_WHAT_TO_DO:
				return "2000";
			case ASSEMBLING_A_GROUP:
				return "3000";
			case WAITING_FOR_GROUP_ARRIVAL:
				return "4000";
			case PRESENTING_THE_REPORT:
				return "5000";
			default:
				return "null";
		}
	}

	/**
	 * Converts the state of the thieves to numbers.
	 *
	 * @param state thief state
	 * @return state number reference or null
	 */
	public String getThiefStateNum(State_Thief state) {
		switch (state) {
			case OUTSIDE:
				return "1000";
			case CRAWLING_INWARDS:
				return "2000";
			case AT_A_ROOM:
				return "3000";
			case CRAWLING_OUTWARDS:
				return "4000";
			case HEAR_REPORT:
				return "5000";
			default:
				return "null";
		}
	}
}
