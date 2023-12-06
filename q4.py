

with open("in.txt", "r") as fp:
    sum_pts = 0
    num_each_card = {}
    
    lines = list(fp.readlines())
    for i in range(len(lines)):
        num_each_card[i+1] = 1
    
    cur_card = 1
    for line in lines:
        
        winning_nums = set()
        line = line.strip()
        
        cards = line[line.index(":")+1:].strip().split("| ")
        # winning card
        for n in cards[0].strip().split(" "):
            if n.strip() == "": continue
            
            winning_nums.add(int(n.strip()))
        # numbers card
        cur_sum = 0
        num_nums_that_won_hooray = 0
        for n in cards[1].strip().split(" "):
            
            if n.strip() == "": continue
            
            num = int(n.strip())
            if num in winning_nums:
                num_nums_that_won_hooray += 1
                
                if cur_sum == 0:
                    cur_sum = 1
                else:
                    cur_sum *= 2
        
        for i in range(1, num_nums_that_won_hooray+1):
            if cur_card + i not in num_each_card: break
            num_each_card[cur_card + i] += num_each_card[cur_card]
            
        cur_card += 1
                
        sum_pts += cur_sum
        
    # print(num_each_card)
        
    print("Part 1: ", sum_pts)
    print("Part 2: ", sum(num_each_card.values()))