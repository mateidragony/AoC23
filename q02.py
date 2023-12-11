with open("in.txt", "r") as fp:
    possIds = []
    power = []
    id = 1
    for line in fp:
        numColor = {"red": 0, "green": 0, "blue": 0}
        
        picks = line[line.index(':')+1:].strip().split("; ")
        for pick in picks:
            colors = pick.strip().split(", ")
            for color in colors:
                data = color.strip().split(" ")
                # print("data ",data)
                numColor[data[1]] = max(int(data[0]), numColor[data[1]])   
        
        if numColor["red"] <= 12 and numColor["green"] <= 13 and numColor["blue"] <= 14:
            possIds.append(id)
        power.append(numColor["red"] * numColor["blue"] * numColor["green"])        
             
        id+=1
    print("Part 1: ", sum(possIds))
    print("Part 2: ", sum(power))