import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static final int max_transitions = 20;
    private static boolean accepted = false;

    public static void main(String[] args) {

        System.out.println("Enter your Turing Machine Encoding and your string: ");

        Scanner in = new Scanner(System.in);

        String input = in.nextLine();

        if (input.equals("") || input.equals(" ")) {
            System.out.println("e");
        }
        else {

            System.out.println();

            ArrayList<String> parsed_input = parseinput(input);


            int num_of_transitions = parsed_input.size() - 3;
            String input_string = parsed_input.get(parsed_input.size() - 1);

            //create an array list of transitions
            ArrayList<String> transition = trim_to_transitions(parsed_input);

            //line 1
            print_encoding(parsed_input);
            //line2
            System.out.println(input_string);
            //line3
            System.out.println(num_of_transitions);
            //line4
            ArrayList<String> states = find_number_of_states(transition);


            Configuration object = new Configuration();
            object.head_position = 0;
            object.current_state = "q0";
            object.tape = input_string;
            object.depth = 0;

            input_string = input_string + "_";

            ArrayList<Configuration> result = acceptance(object, transition, input_string);

            result = remove_duplicates(result);

            ArrayList<Configuration> new_result = new ArrayList<>();

            boolean accepted = false;

            for (int k = 0; k < 20; k++) {
                if (!accepted) {

                    for (int j = 0; j < result.size(); j++) { //for every configuration in result.

                        Configuration current_config = result.get(j);

                        if (current_config.current_state.equals("qa")) {

                            //if you see a config with accept, break out of the loop
                            System.out.println("M stops and accepts w");
                            accepted = true;
                            break;

                        } else
                        //all other configurations, non accepted
                        {
                            //for any other configuration, call acceptance, and make a list of new configs
                            //replace the current_config with the new configurations
                            //keep the rejected configurations
                            ArrayList<Configuration> temp = new ArrayList<>();

                            if ((!current_config.current_state.equals("qr")) && current_config.depth < 20) {
                                //don't send rejected configs to the function

                                temp = acceptance(current_config, transition, input_string);

                                if (!temp.isEmpty()) {
                                    //remove duplicate configurations if temp is not empty
                                    temp = remove_duplicates(temp);
                                }

                                result.remove(current_config);
                                new_result.remove(current_config);

                                new_result = check_for_same_objects(temp, new_result);

                            }
                        }
                    }
                }

                result.addAll(new_result);
                result = remove_duplicates(result);

            }

            boolean rej = false;

            if (!accepted) {

                for (int k = 0; k < result.size(); k++) {
                    if (result.get(k).current_state.equals("qr")) {
                        rej = true;
                    } else
                        rej = false;
                }

            }

            if (rej == true) {
                System.out.println("M stops and rejects w");
            }


            if (!accepted && !rej) {
                System.out.println("M is still running");
            }
        }

    }

    private static ArrayList<Configuration> remove_duplicates(ArrayList<Configuration> result) {

        for (int j = 0; j < result.size() - 1; j++) {

            for (int i = 1; i < result.size(); i++) {

                Configuration current = result.get(j);
                Configuration last_saved = result.get(i);

                if (current.head_position == last_saved.head_position
                        && current.depth == last_saved.depth
                        && (current.tape.equals(last_saved.tape))
                        && (current.current_state.equals(last_saved.current_state))) {

                    result.remove(i);
                }
            }
        }

        return result;
    }

    private static ArrayList<Configuration> check_for_same_objects(ArrayList<Configuration> temp, ArrayList<Configuration> new_result) {

        for (int i = 0; i < new_result.size(); i++) {
            for (int j = 0; j < temp.size(); j++) {
                if (new_result.get(i).current_state.equals(temp.get(j).current_state)
                        && new_result.get(i).tape.equals(temp.get(j).tape)
                        && new_result.get(i).depth == temp.get(j).depth
                        && new_result.get(i).head_position == temp.get(j).head_position)

                    temp.remove(j);
            }
        }

        new_result.addAll(temp);

        return new_result;

    }

    private static ArrayList<Configuration> acceptance(Configuration initial,
                                                       ArrayList<String> transition,
                                                       String input_string) {

        ArrayList<Configuration> temp = new ArrayList<>();

            if (!initial.current_state.equals("qr")) {

                String current_s = initial.current_state;
                String input_under_head;
                if (initial.head_position >= input_string.length()) {
                    input_under_head = "_";
                }
                else
                    input_under_head = Character.toString(input_string.charAt(initial.head_position));

                int new_depth = initial.depth + 1;

                //start building new configs

                for (String t : transition
                ) {

                    String state_in_t = t.substring(0, 2);
                    String expected_string = t.substring(3, 4);
                    String next_state = t.substring(6, 8);
                    String updated_input_char = t.substring(9, 10);
                    String direction = t.substring(11, 12);

                    if ((current_s.equals(state_in_t)) && (expected_string.equals(input_under_head))) {
                        //add a new linkedlist to list of linkedlists

                        Configuration new_config = new Configuration();
                        //update current_state
                        new_config.current_state = next_state;
                        //update head position
                        if (direction.equals("R")) {
                                new_config.head_position = initial.head_position + 1;
                        } else if (direction.equals("L")) {
                            new_config.head_position = initial.head_position - 1;
                        }
                        //update tape
                        StringBuilder new_tape = new StringBuilder(input_string);
                        if (!updated_input_char.equals("_")) {
                            new_tape.setCharAt(initial.head_position, updated_input_char.charAt(0));
                        }

                        new_config.tape = new_tape.toString();
                        new_config.depth = new_depth;

                        temp.add(new_config);
                    }
                }

            }


        return temp;
    }

    private static void print_encoding(ArrayList<String> parsed_input) {

        for (int i = 1; i < (parsed_input.size() - 2); i++) {
            System.out.print("#" + parsed_input.get(i));
        }
        System.out.print("# \n");
    }

    private static ArrayList<String> find_number_of_states(ArrayList<String> transition) {
        ArrayList<String> states = new ArrayList<String>();

        for (int j = 0; j < transition.size(); j++) {
            String temp = transition.get(j).substring(0, 2);
            String temp2 = transition.get(j).substring(6, 8);

            if (!states.contains(temp)) {
                states.add(temp);
            }
            if (!states.contains(temp2)) {
                states.add(temp2);
            }
        }

        System.out.println(states.size());

        return states;
    }

    private static ArrayList<String> trim_to_transitions(ArrayList<String> parsed_input) {

        ArrayList<String> transition = new ArrayList<String>();

        for (int i = 1; i < parsed_input.size() - 2; i++) {
            transition.add(parsed_input.get(i));
        }

        return transition;
    }

    private static ArrayList<String> parseinput(String input) {

        ArrayList<String> parsed_input = new ArrayList<String>(Arrays.asList(input.split("#")));

        return parsed_input;
    }
}

class Configuration {
    int depth = 0;
    String current_state;
    String tape;
    int head_position;
}