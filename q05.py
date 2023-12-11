with open("in.txt", "r") as fp:
    
    lines = fp.readlines()
    
    seeds = list(map(lambda e: (int(e), False), lines[0][lines[0].index(":")+1:].strip().split(" ")))
    old_seeds = seeds.copy()
    
    lineNo = 3
    while(lineNo < len(lines)):
        
        # while in numbers
        while(lines[lineNo][0].isdigit()):
            
            nums = list(map(lambda e: int(e), lines[lineNo].strip().split(" ")))
            destStart = nums[0]
            srcStart = nums[1]
            rangeLen = nums[2]
            
            
            for i in range(len(old_seeds)):
                if old_seeds[i][0] >= srcStart and old_seeds[i][0] < srcStart + rangeLen and not old_seeds[i][1]:
                    old_seeds[i] = old_seeds[i][0] - srcStart + destStart, True
                    
            i = 0
            while i < len(seeds):
                if not seeds[i][1]:
                    numbersLine = sorted([srcStart, srcStart + rangeLen, seeds[i][0], seeds[i][0]+seeds[i+1][0]])
                    
                    curSeed = seeds.pop(i)
                    curSeedLen = seeds.pop(i)
                    
                    noneUnchanged = True
                    for idx in range(len(numbersLine)):
                        if numbersLine[idx] >= curSeed[0] and numbersLine[idx] < curSeed[0] + curSeedLen[0]: # Good numbers
                            # Changed
                            if numbersLine[idx] >= srcStart and numbersLine[idx] < srcStart + rangeLen:
                                seeds.append((numbersLine[idx] - srcStart + destStart, True))
                                seeds.append((numbersLine[idx+1] - numbersLine[idx], True))
                            # Unchanged
                            else:
                                seeds.insert(i, (numbersLine[idx], False))
                                seeds.insert(i+1, (numbersLine[idx+1] - numbersLine[idx], False))
                                noneUnchanged = False
                    if noneUnchanged: i-=2
                
                i+=2

                    
            lineNo += 1
            if(lineNo >= len(lines)): break
            
        for i in range(len(seeds)):
            seeds[i] = seeds[i][0], False
                
        for i in range(len(old_seeds)):
            old_seeds[i] = old_seeds[i][0], False
        
        lineNo += 2

# print(list(map(lambda e: e[0],seeds))[::2])
print("Part 1: ", min(list(map(lambda e: e[0],old_seeds))))
print("Part 2: ",min(list(map(lambda e: e[0],seeds))[::2]))


