package groups;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	static String[] names;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader readFile = null;
		try {
			readFile = new BufferedReader(new FileReader("input.txt"));
		} catch (Exception e) {
			System.out.println("input.txt is missing");
			System.exit(0);
		}
		String line = readFile.readLine();
		String total = "";
		while (line != null) {
			total += line + "\n";
			line = readFile.readLine();
		}
		readFile.close();
		if (total.equals("")) {
			System.out.println("input.txt is empty");
			System.exit(0);
		}
		names = total.split("\n");
		int sessions = 0, groupSize = 0, blanks = 0;
		Scanner input = new Scanner(System.in);
		boolean flag = true;
		while (flag) {
			try {
				System.out.println("Enter how many group sessions you wish to generate");
				sessions = input.nextInt();
				if (sessions <= 0) {
					flag = true;
				} else {
					flag = false;
				}
			} catch (Exception e) {
				System.out.println("Input Missmatch");
				input.nextLine();
				flag = true;
			}
		}
		flag = true;
		while (flag) {
			try {
				System.out.println("Enter group size");
				groupSize = input.nextInt();
				if (groupSize > names.length || groupSize < 0) {
					flag = true;
				} else {
					flag = false;
				}
			} catch (Exception e) {
				System.out.println("Input Missmatch");
				input.nextLine();
				flag = true;
			}
		}
		if (names.length % groupSize != 0) {
			input.nextLine();
			if (names.length % groupSize == 1) {
				System.out.println("Your body is not divisible by the specified group size (There is a remainder of 1 person)");
			} else {
				System.out.println("Your body is not divisible by the specified group size (There is a remainder of " + names.length % groupSize + " people)");
			}
			if (names.length % groupSize > groupSize / 2.0) {
				System.out.println("Do you wish to have a few larger groups(Enter 1) or a few smaller groups(Recommended)(Enter 2) to fix this?");
			} else {
				System.out.println("Do you wish to have a few larger groups(Recommended)(Enter 1) or a few smaller groups(Enter 2) to fix this?");
			}
			String temp = input.nextLine();
			flag = false;
			do {
				if (temp.equals("1")) {
					// larger groups

					blanks = names.length / groupSize - names.length % groupSize;
					groupSize++;
					flag = false;
				} else if (temp.equals("2")) {
					// smaller groups
					blanks = groupSize - names.length % groupSize;
					flag = false;
				} else {
					flag = true;
					System.out.println("Your body is not divisible by the specified group size");
					System.out.println("Do you wish to have larger groups (Enter 1) or smaller groups?(Enter 2)");
					temp = input.nextLine();
				}
			} while (flag);
		}
		BufferedWriter writeFile = new BufferedWriter(new FileWriter("output.txt"));
		writeFile.write(groupFind(names.length, sessions, groupSize, blanks));
		System.out.println("Group schedule written to output.txt");
		writeFile.flush();
		writeFile.close();
		input.close();

	}

	public static String groupFind(int classSize, int sessions, int groupSize, int blanks) {
		if (groupSize == 1) {
			return "Do this yourself";
		}
		int[][] exclusions;
		if (blanks == 0) {
			exclusions = new int[classSize][classSize];
		} else {
			exclusions = new int[classSize + 1][classSize + 1];
		}
		for (int i = 0; i < classSize; i++) {
			for (int z = 0; z < classSize; z++) {
				exclusions[i][z] = 0;
			}
		}
		if (blanks != 0) {
			exclusions[classSize][classSize] = 1000;
		}
		return groupFind(exclusions, sessions, 0, "", groupSize, blanks);
	}

	public static String groupFind(int exclusions[][], int sessions, int session, String groups, int groupSize, int blanks) {
		if (sessions == session) {
			return groups;
		}
		int group[][];
		if (blanks > 0) {
			group = new int[(exclusions.length + blanks - 1) / groupSize][groupSize];
		} else {
			group = new int[(exclusions.length) / groupSize][groupSize];
		}
		boolean used[] = new boolean[exclusions.length];

		int blanksUnused = blanks;
		if (blanks != 0) {
			exclusions[exclusions.length - 1][exclusions.length - 1]++;
		}
		for (int i = 0; i < exclusions.length; i++) {
			used[i] = false;
		}
		for (int i = 0; i < group.length; i++) {
			int current = 0;
			for (int z = 0; z < used.length; z++) {
				if (z == used.length - 1) {
					// this is a blank
					if (blanksUnused > 0) {
						current = z;
						break;
					}
				} else if (!used[z]) {
					current = z;
					break;
				}
			}
			int lowestExclusion = -1;
			int leID = -1; // lowestexclusion id

			for (int z = 0; z < exclusions.length; z++) {
				if ((z != current || (z == used.length - 1 && blanks > 0)) && (!used[z] || (z == used.length - 1 && blanksUnused > 0)) && (lowestExclusion > exclusions[current][z] || lowestExclusion == -1)) {
					lowestExclusion = exclusions[current][z];
					leID = z;
				}
			}
			if (current == used.length - 1) {
				blanksUnused--;
			} else {
				used[current] = true;
			}
			if (leID == used.length - 1) {
				blanksUnused--;
			} else {
				used[leID] = true;
			}
			group[i][0] = current;
			group[i][1] = leID;

			for (int x = 2; x < groupSize; x++) {
				lowestExclusion = -1;
				for (int z = 0; z < exclusions.length; z++) {
					if ((!used[z] || (z == used.length - 1 && blanksUnused > 0))) {
						int netExclusion = 0;
						for (int y = 0; y < x; y++) {
							netExclusion += exclusions[group[i][y]][z];
						}
						if (z != current && (lowestExclusion > netExclusion || lowestExclusion == -1)) {
							lowestExclusion = netExclusion;
							leID = z;
						}
					}
				}

				if (leID == used.length - 1) {
					blanksUnused--;
				} else {
					used[leID] = true;
				}
				group[i][x] = leID;

			}
		}
		boolean flag = false;
		do {
			flag = false;
			// find the group with the largest amount of errors
			int mostErrors = -1;
			int gID = 0; // group with the most errors
			for (int i = 0; i < group.length; i++) {
				int errors = 0;
				for (int z = 0; z < groupSize - 1; z++) {
					for (int x = z + 1; x < groupSize; x++) {
						errors += exclusions[group[i][z]][group[i][x]];
					}
				}
				if (errors > mostErrors) {
					gID = i;
					mostErrors = errors;
				}
			}
			// now try to swap group members to achieve a better solution
			for (int badGroupMember = 0; badGroupMember < groupSize; badGroupMember++) {
				boolean switchMade = false;
				for (int i = 0; i < group.length; i++) {
					// can't swap members of the same group
					if (i != gID) {
						// errors in group you are attempting to swap with
						int groupError = 0;
						for (int z = 0; z < groupSize - 1; z++) {
							for (int x = z + 1; x < groupSize; x++) {
								groupError += exclusions[group[i][z]][group[i][x]];
							}
						}

						for (int groupMember = 0; groupMember < groupSize; groupMember++) {
							// true if the same person is in the other group you
							// are attempting to swap with
							int resultBadGroup = 0, resultGoodGroup = 0;
							for (int z = 0; z < groupSize - 1; z++) {
								for (int x = z + 1; x < groupSize; x++) {

									if (groupMember == x) {
										// switch
										resultGoodGroup += exclusions[group[i][z]][group[gID][badGroupMember]];
									} else if (groupMember == z) {
										// switch
										resultGoodGroup += exclusions[group[gID][badGroupMember]][group[i][x]];
									} else {
										// no switch
										resultGoodGroup += exclusions[group[i][z]][group[i][x]];
									}

									if (badGroupMember == x) {
										// switch
										resultBadGroup += exclusions[group[gID][z]][group[i][groupMember]];
									} else if (badGroupMember == z) {
										// switch
										resultBadGroup += exclusions[group[i][groupMember]][group[gID][x]];
									} else {
										// no switch
										resultBadGroup += exclusions[group[gID][z]][group[gID][x]];
									}
								}
							}
							if (groupError - resultGoodGroup + (mostErrors - resultBadGroup) > 0 && mostErrors - resultBadGroup > 0) {
								// swap them in memory
								int temp = group[i][groupMember];
								group[i][groupMember] = group[gID][badGroupMember];
								group[gID][badGroupMember] = temp;
								// update errors and break from search
								mostErrors = resultBadGroup;
								switchMade = true;
								flag = true;
								break;
							}
						}
						if (switchMade) {
							break;
						}
					}
				}
			}
		} while (flag);
		// add exclusions
		for (int i = 0; i < group.length; i++) {
			for (int z = 0; z < groupSize - 1; z++) {
				for (int x = z + 1; x < groupSize; x++) {
					exclusions[group[i][z]][group[i][x]]++;
					exclusions[group[i][x]][group[i][z]]++;
				}
			}
		}
		groups = groups + "\r\n" + "Session: " + (session + 1);
		for (int i = 0; i < group.length; i++) {
			groups+="\r\n";
			//groups += "\r\n" + "\t Group " + (i + 1) + ":\r\n\t";
			for (int z = 0; z < group[i].length; z++) {
				// groups += (group[i][z] + 1) + "\t";
				if ((group[i][z] != used.length - 1 || blanks == 0)) {
					// not a blank
					// groups += (group[i][z] + 1) + "\t";
					groups += names[group[i][z]];
					groups+= "\r\n";
					//for (int x = 0; x < 30 - (names[group[i][z]].length()); x++) {
					//	groups += " ";
					//}
				}
			}
			groups += "\r\n";
		}
		return groupFind(exclusions, sessions, session + 1, groups, groupSize, blanks);
	}
}
