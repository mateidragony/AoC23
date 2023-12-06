
numbers = ["one", "two", "three", "four", "five", "six", "seven", "eight", "nine"]

def readNumber(strng, i):
    num = None
    for idx, number in enumerate(numbers):
        if i + len(number) >= len(strng): continue
        
        if(strng[i:i+len(number)] == number): return str(idx + 1)
    
    return -1


sum = 0
with open("in1.txt", "r") as fp:
    for line in fp.readlines():
        dig1 = None
        dig2 = None
        for idx, char in enumerate(line):
            
            potentialNum = readNumber(line, idx)
            if potentialNum != -1:
                if dig1 == None:
                    dig1 = potentialNum
                dig2 = potentialNum
            
            if(char.isdigit()):
                if dig1 == None:
                    dig1 = char
                dig2 = char
        print("Num: ",dig1 + dig2)
        sum += int(dig1 + dig2)
        
print(sum)