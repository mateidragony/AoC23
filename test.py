lst = [0,1,2,3,4,5]

i=0
while i < len(lst):
    lst.pop(i)
    lst.insert(i,999+i)
    i+=1
    
print(lst)