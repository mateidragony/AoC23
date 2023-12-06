with open("in.txt", "r") as fp:
    
    lines = fp.readlines()
    
    seeds = list(map(lambda e: (int(e), False), lines[0][lines[0].index(":")+1:].strip().split(" ")))
    
    # updated_seeds = []
    # for i in range(0, len(seeds), 2):
    #     for seed in range(seeds[i][0], seeds[i][0]+seeds[i+1][0]):
    #         updated_seeds.append((seed, False))
    # #print(updated_seeds)
    
    #print(seeds)
    lineNo = 3
    while(lineNo < len(lines)):
        #print(lines[lineNo-1].strip())
        
        # while in numbers
        while(lines[lineNo][0].isdigit()):
            
            nums = list(map(lambda e: int(e), lines[lineNo].strip().split(" ")))
            destStart = nums[0]
            srcStart = nums[1]
            rangeLen = nums[2]
            
            #print("LineNo: ",lineNo, ":","DS: ", destStart, ", SS: ", srcStart, ", RL: ", rangeLen)
            
            # for i in range(len(seeds)):
            #     if seeds[i][0] >= srcStart and seeds[i][0] < srcStart + rangeLen and not seeds[i][1]:
            #         seeds[i] = seeds[i][0] - srcStart + destStart, True
                    
            i = 0
            # if lineNo > 5:    clbreak
            while i < len(seeds):
                # #print("i: ",i,", len seeds: ", len(seeds))
                if not seeds[i][1]:
                    numbersLine = sorted([srcStart, srcStart + rangeLen, seeds[i][0], seeds[i][0]+seeds[i+1][0]])
                    #print("LineNo: ",lineNo, ":"," Looking at:", seeds[i], seeds[i+1])
                    #print("LineNo: ",lineNo, ":",numbersLine)
                    
                    curSeed = seeds.pop(i)
                    curSeedLen = seeds.pop(i)
                    
                    noneUnchanged = True
                    for idx in range(len(numbersLine)):
                        if numbersLine[idx] >= curSeed[0] and numbersLine[idx] < curSeed[0] + curSeedLen[0]: # Good numbers
                            # Changed
                            #print("LineNo: ",lineNo, ":","Good Numbers: (", numbersLine[idx], ", ",numbersLine[idx+1],")")
                            
                            if numbersLine[idx] >= srcStart and numbersLine[idx] < srcStart + rangeLen:
                                seeds.append((numbersLine[idx] - srcStart + destStart, True))
                                seeds.append((numbersLine[idx+1] - numbersLine[idx], True))
                                #print("LineNo: ",lineNo, ":","changed")
                            # Unchanged
                            else:
                                seeds.insert(i, (numbersLine[idx], False))
                                seeds.insert(i+1, (numbersLine[idx+1] - numbersLine[idx], False))
                                #print("LineNo: ",lineNo, ":","Unchanged")
                                noneUnchanged = False
                        # #print(idx)
                    if noneUnchanged: i-=2
                    #print("LineNo: ",lineNo, ":",seeds)
                    # remove original from list
                
                i+=2
                # if seeds[i][0] >= srcStart and seeds[i][0] < srcStart + rangeLen and not seeds[i][1]:
                #     seeds[i] = seeds[i][0] - srcStart + destStart, True
                    
            # for i in range(len(updated_seeds)):
            #     if updated_seeds[i][0] >= srcStart and updated_seeds[i][0] < srcStart + rangeLen and not updated_seeds[i][1]:
            #         updated_seeds[i] = updated_seeds[i][0] - srcStart + destStart, True
            
            lineNo += 1
            if(lineNo >= len(lines)): break
            
        # #print(seeds)
        for i in range(len(seeds)):
            seeds[i] = seeds[i][0], False
                
        #print("Seed reset for next line: ", seeds)
            
        # for i in range(len(updated_seeds)):
        #     updated_seeds[i] = updated_seeds[i][0], False
        
        lineNo += 2

print(list(map(lambda e: e[0],seeds))[::2])
print(min(list(map(lambda e: e[0],seeds))[::2]))
# #print(min(new_seeds))

# #print()