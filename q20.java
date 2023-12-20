/*  
 *  For some reason my in.txt doesn't have a loop before 1000 :(
 *  This problem could be made better by having the elves press 
 *  the button much more that 1000 times to force you to use a 
 *  memo.
 * 
 *  Not a fan of part 2. I don't like how you have to draw it
 *  out to see the pattern of what's going on. I find that
 *  gimmicky. I would've much preferred a general solution :(
 *  but that's just my two cents
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class q20 {

    public static final String filename = "in.txt";

    public static enum Pulse{
        LOW, HIGH, NOT_POSSIBLE, ANY;
    }

    public static enum State{
        ON, OFF; 
    }

    // Reason for sourcing: I was lazy and didn't want to write this math code myself...
    private static long gcd(long a, long b){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        while (b > 0){
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    private static long lcm(long a, long b){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        return a * b / gcd(a, b);
    }

    private static long lcm(long[] input){ // Source: Jeffrey Hantin Stack Overflow https://stackoverflow.com/questions/4201860/how-to-find-gcd-lcm-on-a-set-of-numbers
        long result = input[0];
        for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
        return result;
    }

    public static HashMap<String, Module> initModules(List<String> lines){
        HashMap<String, Module> modules = new HashMap<>();
        
        for(String line : lines) Module.readModuleStr(line, modules); // initialize all of the modules
        for(Module m : modules.values()) m.initializeInputs(modules); // initialize their inputs
        for(Module m : modules.values()) if(m instanceof Conjunction) ((Conjunction)m).initPulseMemory(); // initialize the pulse memories

        return modules;
    }

    public static int[] pushButton(HashMap<String, Module> modules, long numPresses, List<String> mustTurnHigh, HashMap<String, Long> numCyclesToTrunHigh){
        Queue<Signal> signalsToProcess = new LinkedList<>(); // regular signal queue
        signalsToProcess.add(new Signal(Pulse.LOW, new Broadcaster("Button"), modules.get("broadcaster"))); // button sends signal

        int[] numSignals = new int[2]; // high, low
        numSignals[1] = 1; // button's low pulse

        while(!signalsToProcess.isEmpty()){

            Signal signal = signalsToProcess.poll();
            List<Signal> newSignals = signal.receiver.handleSignal(modules, signal);
            
            if(mustTurnHigh != null && !newSignals.isEmpty()){
                if(mustTurnHigh.contains(signal.sender.name)){ // one of the four
                    if(((Conjunction)signal.sender).pulseMemory.values().stream().allMatch(e -> e == Pulse.HIGH) && !numCyclesToTrunHigh.containsKey(signal.sender.name)){
                        numCyclesToTrunHigh.put(signal.sender.name, numPresses);
                    }
                }
            }

            for(Signal newSignal : newSignals){
                signalsToProcess.add(newSignal);
                if(newSignal.pulse == Pulse.HIGH) numSignals[0]++;
                if(newSignal.pulse == Pulse.LOW) numSignals[1]++;
            }
        }

        return numSignals;  
    }

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File(filename));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        // --------------------
        //      Part One
        // --------------------

        HashMap<String, Module> modules = initModules(lines);

        HashMap<String, int[]> stateMemo = new HashMap<>();
        List<String> stateMemoOrder = new ArrayList<>();

        int[] highLow = pushButton(modules, 0, null, null);
        String hashStr = Module.getModulesHashStr(modules);

        int[] cycleHighLow = new int[2]; // high, low
        int totalButtonPresses = 1000;

        while(!stateMemo.containsKey(hashStr) && stateMemo.size() < totalButtonPresses){
            stateMemo.put(hashStr, highLow);
            stateMemoOrder.add(hashStr);

            highLow = pushButton(modules, 0, null, null);
            hashStr = Module.getModulesHashStr(modules);

            cycleHighLow[0] += highLow[0];
            cycleHighLow[1] += highLow[1];
        }


        int numCycles = totalButtonPresses / stateMemo.size();
        int leftOver = totalButtonPresses - stateMemo.size() * numCycles;

        int[] leftOverHighLow = new int[2]; // high, low
        for(int i=0; i<leftOver; ++i){
            int[] curHighLow = stateMemo.get(stateMemoOrder.get(i));
            leftOverHighLow[0] += curHighLow[0];
            leftOverHighLow[1] += curHighLow[2];
        }

        long partOneSum = (cycleHighLow[0] * cycleHighLow[1] * numCycles * numCycles) + (leftOverHighLow[0] * leftOverHighLow[1] * leftOver * leftOver);

        // --------------------
        //      Part Two
        // --------------------

        modules = initModules(lines); // reset modules

        List<String> rxInputs = new ArrayList<>();
        for(Module m : modules.values()) if(m.outputs.contains("rx")) rxInputs.add(m.name);
        Module rx = new OutputModule("rx");
        rx.inputs = rxInputs;
        modules.put("rx", rx);

        List<String> mustAllHigh = new ArrayList<>(); // all inputs must be high
        // Hardcoded: rx -> lx -> [rp, cl, nj, lb]
        for(String in : modules.get(rx.inputs.get(0)).inputs){
            mustAllHigh.add(modules.get(in).inputs.get(0));
        }
        
        HashMap<String, Long> numCyclesToTurnHigh = new HashMap<>();

        long numPushes = 0;
        while(numCyclesToTurnHigh.size() < mustAllHigh.size()){
            pushButton(modules, numPushes, mustAllHigh, numCyclesToTurnHigh);
            numPushes++;
        }

        System.out.println("Part 1: " + partOneSum);
        System.out.println("Part 2: "+lcm(numCyclesToTurnHigh.values().stream().mapToLong(e -> e+1).toArray()));
    }

    private static abstract class Module {
        String name;
        List<String> inputs;
        List<String> outputs;

        public Module(String name){
            this.name = name;
            inputs = new ArrayList<>(); // no gonna know all inputs until I've created all the modules, handled in main
            outputs = new ArrayList<>();
        }

        public static void readModuleStr(String moduleStr, HashMap<String, Module> allModules){
            String[] inOut = moduleStr.split(" -> ");
            String moduleName = moduleStr.charAt(0) == 'b' ? inOut[0] : inOut[0].substring(1);

            char type = moduleStr.charAt(0);
            Module module = type == 'b' ? new Broadcaster(moduleName) : type == '%' ? new FlipFlop(moduleName) : new Conjunction(moduleName);
            module = allModules.getOrDefault(moduleName, module); // If that module name is already in my hashmap

            module.initializeOutputs(inOut[1]);
            allModules.put(moduleName, module);
        }

        public void initializeInputs(HashMap<String, Module> allModules){
            // initialize the inputs of my outputs
            for(String out : outputs){
                if(!allModules.containsKey(out)) continue;  // Output module (will handle later)
                allModules.get(out).inputs.add(this.name);
            }
        }
        
        public void initializeOutputs(String outputsStr){
            // String in the format of %a -> b
            for(String out : outputsStr.split(", ")) outputs.add(out);
        }

        public List<Signal> sendPulse(HashMap<String, Module> allModules, Pulse sendPulse){ 
            List<Signal> signalsToSend = new ArrayList<>();
            for(String out : outputs){
                signalsToSend.add(new Signal(sendPulse, this, allModules.getOrDefault(out, new OutputModule(out)))); // null is output module
            }

            return signalsToSend;
        }

        public abstract List<Signal> handleSignal(HashMap<String, Module> allModules, Signal signal);

        public static String getModulesHashStr(HashMap<String, Module> allModules){
            StringBuilder str = new StringBuilder();

            for(Module m : allModules.values()){
                if(m instanceof FlipFlop) str.append(m.name).append(((FlipFlop)m).state);
            }

            return str.toString();
        }

        @Override
        public String toString(){
            return "{"+name+", "+inputs+", "+outputs+"}";
        }
    } 

    private static class FlipFlop extends Module{

        State state;

        public FlipFlop(String name){
            super(name);
            this.state = State.OFF;
        }

        public List<Signal> handleSignal(HashMap<String, Module> allModules, Signal signal){
            if(signal.pulse == Pulse.HIGH) return new ArrayList<>();
            Pulse sendPulse = state == State.OFF ? Pulse.HIGH : Pulse.LOW;
            state = state == State.OFF ? State.ON : State.OFF; // swap state
            return sendPulse(allModules, sendPulse);
        }
    }

    private static class Conjunction extends Module{

        HashMap<String, Pulse> pulseMemory;

        public Conjunction(String name){
            super(name);
            pulseMemory = new HashMap<>();
        }

        public void initPulseMemory(){ // Only once all inputs have been set
            for(String in : inputs) pulseMemory.put(in, Pulse.LOW);
        }

        public List<Signal> handleSignal(HashMap<String, Module> allModules, Signal signal){
            pulseMemory.put(signal.sender.name, signal.pulse);
            Pulse sendPulse = pulseMemory.values().stream().allMatch(e -> e == Pulse.HIGH) ? Pulse.LOW : Pulse.HIGH;
            return sendPulse(allModules, sendPulse);
        }

    }

    private static class Broadcaster extends Module{

        public Broadcaster(String name){
            super(name);
        }

        public List<Signal> handleSignal(HashMap<String, Module> allModules, Signal signal){
            return sendPulse(allModules, signal.pulse);
        }
    }

    private static class OutputModule extends Module{

        public OutputModule(String name) {
            super(name);
        }

        @Override
        public List<Signal> handleSignal(HashMap<String, Module> allModules, Signal signal) {
            return new ArrayList<>();
        }
    }

    private static class Signal{
        Pulse pulse;
        Module sender;
        Module receiver;
        
        public Signal(Pulse pulse, Module sender, Module receiver) {
            this.pulse = pulse;
            this.sender = sender;
            this.receiver = receiver;
        }

        @Override
        public String toString(){
            return pulse + " " + sender.toString() + " " + receiver.toString();
        }
    }
}