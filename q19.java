import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;


public class q19 {

    static Set<Integer> universalSet = new HashSet<>(IntStream.range(1, 4001).boxed().toList());

    // If i find a dest of R or A put function in there
    public static Set<Integer> union(Set<Integer> set1, Set<Integer> set2){
        Set<Integer> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    public static Set<Integer> interest(Set<Integer> set1, Set<Integer> set2){
        Set<Integer> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    public static Set<Integer> difference(Set<Integer> set1, Set<Integer> set2){
        Set<Integer> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    public static Function<Part, Boolean> negate(Function<Part, Boolean> before){
        return part -> !before.apply(part);
    }

    public static Function<Part, Boolean> combineRule(Function<Part, Boolean> rule1, Function<Part, Boolean> rule2){
        if(rule1 == null) return rule2;
        if(rule2 == null) return rule1;
        return part -> rule1.apply(part) && rule2.apply(part);
    }

    public static Function<Part, Boolean> createRule(String afterStr){
        // afterStr should be a string like so a<2006
        Function<Part, Boolean> after = null;
        char partVar = afterStr.charAt(0);
        int numNeeded = Integer.parseInt(afterStr.substring(2));
        if(afterStr.contains(">")){
            switch(partVar){
                case 'x' -> after = part -> part.x > numNeeded;
                case 'm' -> after = part -> part.m > numNeeded;
                case 'a' -> after = part -> part.a > numNeeded;
                case 's' -> after = part -> part.s > numNeeded;
            }
        } else {
            switch(partVar){
                case 'x' -> after = part -> part.x < numNeeded;
                case 'm' -> after = part -> part.m < numNeeded;
                case 'a' -> after = part -> part.a < numNeeded;
                case 's' -> after = part -> part.s < numNeeded;
            }
        }

        return after;
    }

    public static WorkflowRule negateWorkflowRule(WorkflowRule before){
        WorkflowRule negated = new WorkflowRule(before.dest);

        // go through each set
        for(int i=0; i<4; ++i){
            if(!universalSet.equals(before.allowedNums.get(i))) {
                negated.allowedNums.set(i, difference(universalSet, before.allowedNums.get(i)));
            }
        }

        return negated;
    }

    public static WorkflowRule combineWorkflowRule(WorkflowRule rule1, WorkflowRule rule2, String dest){
        WorkflowRule combined = new WorkflowRule(dest);

        for(int i=0; i<4; ++i){
            combined.allowedNums.set(i, interest(rule1.allowedNums.get(i), rule2.allowedNums.get(i)));
        }

        return combined;
    }

    public static WorkflowRule createWorkflowRule(String afterStr, String dest){
        // afterStr should be a string like so a<2006
        WorkflowRule newWFR = new WorkflowRule(dest);
        char partVar = afterStr.charAt(0);
        int numNeeded = Integer.parseInt(afterStr.substring(2));

        int setIdx = -1;
        switch(partVar){
            case 'x' -> setIdx = 0;
            case 'm' -> setIdx = 1;
            case 'a' -> setIdx = 2;
            case 's' -> setIdx = 3;
        }

        Set<Integer> ruleAdherent;
        if(afterStr.contains(">")){
            ruleAdherent = new HashSet<>(IntStream.range(numNeeded+1, 4001).boxed().toList());
        } else {            
            ruleAdherent = new HashSet<>(IntStream.range(1, numNeeded).boxed().toList());
        }

        newWFR.allowedNums.set(setIdx, ruleAdherent);

        return newWFR;
    }

    public static long expandPath(WorkflowRule curPath, HashMap<String, List<WorkflowRule>> wfRules, String curWF){
        if(curWF.equals("A")) return curPath.numPoss();
        else if(curWF.equals("R")) return 0;
        
        long numPoss = 0;
        List<WorkflowRule> nextRules = wfRules.get(curPath.dest);
        for(WorkflowRule workflow : nextRules){
            WorkflowRule newRule = combineWorkflowRule(workflow, curPath, workflow.dest);
            if(newRule.numPoss() == 0) continue;

            numPoss += expandPath(newRule, wfRules, workflow.dest);
        }
        return numPoss;
    }
    

    public static void main(String[] args) throws FileNotFoundException{
        Scanner s = new Scanner(new File("in.txt"));
        List<String> lines = new ArrayList<>();
        while(s.hasNextLine()){
            lines.add(s.nextLine());
        }

        List<String> workflowsStrs = new ArrayList<>();
        List<Part> parts = new ArrayList<>();
        boolean readingWorkflows = true;
        for(String line : lines){
            if(line.isBlank()) readingWorkflows = false;
            else if(readingWorkflows) workflowsStrs.add(line);
            else {
                line = line.substring(1, line.length()-1);
                String[] partDatas = line.split(",");
                Part part = new Part();
                part.x = Integer.parseInt(partDatas[0].split("=")[1]);
                part.m = Integer.parseInt(partDatas[1].split("=")[1]);
                part.a = Integer.parseInt(partDatas[2].split("=")[1]);
                part.s = Integer.parseInt(partDatas[3].split("=")[1]);
                parts.add(part);
            }
        }

        // --------------------
        //      Part One
        // --------------------

        HashMap<String, List<Workflow>> workflowRules = new HashMap<>();

        for(String workflowStr : workflowsStrs){
            Function<Part, Boolean> curRule = null;
            
            String curWorkflowName = workflowStr.substring(0, workflowStr.indexOf("{"));
            workflowStr = workflowStr.substring(workflowStr.indexOf("{")+1, workflowStr.length()-1);
            String[] workflows = workflowStr.split(",");

            workflowRules.put(curWorkflowName, new ArrayList<>());

            for(String workflow : workflows){

                if(!workflow.contains(":")){
                    workflowRules.get(curWorkflowName).add(new Workflow(curRule, workflow)); // Else
                    break;
                }

                String[] workflowData = workflow.split(":");
                Function<Part, Boolean> workflowRule = createRule(workflowData[0]);
                String dest = workflowData[1];

                workflowRules.get(curWorkflowName).add(new Workflow(combineRule(curRule, workflowRule), dest));
                curRule = combineRule(curRule, negate(workflowRule)); // Else if
            }
        }

        int partOneSum = 0;

        for(Part part : parts){
            String curWorkflowName = "in";
            while(!curWorkflowName.equals("R") && !curWorkflowName.equals("A")){
                List<Workflow> workflows = workflowRules.get(curWorkflowName);
                for(Workflow workflow : workflows){
                    if(workflow.rule.apply(part)){
                        curWorkflowName = workflow.dest;
                        break;
                    }
                }
            }
            if(curWorkflowName.equals("A"))
                partOneSum += part.x + part.m + part.a + part.s;
        }


        // --------------------
        //      Part Two
        // --------------------

        HashMap<String, List<WorkflowRule>> workflowRulesP2 = new HashMap<>();

        for(String workflowStr : workflowsStrs){
            WorkflowRule curRule = new WorkflowRule(null);

            String curWorkflowName = workflowStr.substring(0, workflowStr.indexOf("{"));
            workflowStr = workflowStr.substring(workflowStr.indexOf("{")+1, workflowStr.length()-1);
            String[] workflows = workflowStr.split(",");

            workflowRulesP2.put(curWorkflowName, new ArrayList<>());

            for(String workflow : workflows){

                if(!workflow.contains(":")){
                    curRule.dest = workflow;
                    workflowRulesP2.get(curWorkflowName).add(curRule); // Else
                    break;
                }

                String[] workflowData = workflow.split(":");
                String dest = workflowData[1];
                WorkflowRule workflowRule = createWorkflowRule(workflowData[0], dest);

                workflowRulesP2.get(curWorkflowName).add(combineWorkflowRule(workflowRule, curRule, dest));
                curRule = combineWorkflowRule(curRule, negateWorkflowRule(workflowRule), null); // Else if
            }
        }

        List<WorkflowRule> possiblePaths = workflowRulesP2.get("in");
        long partTwoSum = 0;

        for(WorkflowRule wf : possiblePaths){
            partTwoSum += expandPath(wf, workflowRulesP2, "in");
        }


        System.out.println("Part 1: "+partOneSum);
        System.out.println("Part 2: "+partTwoSum);
    }


    private static class Part{
        int x, m, a, s;

        @Override
        public String toString(){
            return "{"+x+","+m+","+a+","+s+"}";
        }
    }

    private static class Workflow{
        Function<Part, Boolean> rule;
        String dest;
        public Workflow(Function<Part, Boolean> rule, String dest){
            this.rule = rule;
            this.dest = dest;
        }

        @Override
        public String toString(){
            return "{"+dest+","+rule+"}";
        }
    }

    private static class WorkflowRule{
        // x, m, a, s
        List<Set<Integer>> allowedNums;
        String dest;

        // inclusive inclusive
        public WorkflowRule(String dest) {
            allowedNums = new ArrayList<>();
            for(int i=0; i<4; ++i){
                Set<Integer> set = new HashSet<>();

                for(int j=1; j<=4000; ++j) set.add(j);

                allowedNums.add(set);
            }

            this.dest = dest;
        }

        public long numPoss(){
            long num = 1;
            for(Set<Integer> set : allowedNums) num *= set.size();
            return num;
        }

        @Override
        public String toString(){
            return "Dest: "+dest
                    +"\n\t"+allowedNums.get(0)
                    +"\n\t"+allowedNums.get(1)
                    +"\n\t"+allowedNums.get(2)
                    +"\n\t"+allowedNums.get(3);
        }
    }
}