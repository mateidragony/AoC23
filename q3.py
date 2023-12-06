
valid_symbols = "!@#$%^&*()_-+={}[]/"


def checkAround(i, j, grid, cur_num, potential_gears):
    
    is_nearby = False
    
    for r in range(i-1, i+2):
        for c in range(j-1, j+2):
            if(r >= 0 and c >= 0 and r < len(grid) and c < len(grid[0])):
                if grid[r][c] in valid_symbols:
                    is_nearby = True
                    
                    if grid[r][c] == "*":
                        potential_gears[(r,c)].append(cur_num)
                    
    return is_nearby

with open("in.txt", "r") as fp:
    
    grid = []
    potential_gears = {}
    numbers = []
    temp_num = ""
    sum_parts = 0
    sum_gr = 0
    
    i=0 
    j=0
    
    for line in fp:
        temp = []
        for char in line.strip():
            temp.append(char)
            if char.isdigit():
                temp_num += char
            elif temp_num != "":
                numbers.append(int(temp_num))
                temp_num = ""
                
            if char == "*":
                potential_gears[(i,j)] = []
                
            j+=1    
            
        grid.append(temp)
        i+=1
        j=0
    
    cur_num = 0
    
    print(grid)
    
    for i in range(len(grid)):
        j = 0
        while j < len(grid[i]):
            if grid[i][j].isdigit():
                
                for digit_col in range(j, j+len(str(numbers[cur_num]))):
                    if(checkAround(i, digit_col, grid, numbers[cur_num], potential_gears)):
                        sum_parts += numbers[cur_num]
                        break
                
                j += len(str(numbers[cur_num]))
                cur_num += 1
            j += 1
                
    print(potential_gears)
    
    for gear in potential_gears.values():
        if len(gear) == 2:
            sum_gr += gear[0] * gear[1]
            
    print("sum_parts: ", sum_parts)
    print("sum_gr: ",sum_gr)