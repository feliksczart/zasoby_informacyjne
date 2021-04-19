import numpy as np

L1R  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L1  = [0, 1, 1, 0, 1, 0, 0, 0, 0, 0]
L2  = [1, 0, 0, 1, 0, 0, 0, 0, 0, 0]
L3R  = [0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
L3  = [0, 1, 0, 0, 0, 0, 1, 0, 0, 0]
L4  = [0, 1, 1, 0, 0, 0, 0, 0, 0, 0]
L5  = [0, 0, 0, 0, 0, 1, 1, 0, 0, 0]
L6  = [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
L7  = [0, 0, 0, 0, 1, 1, 1, 1, 1, 1]
L8  = [0, 0, 0, 0, 0, 0, 1, 0, 1, 0]
L9  = [0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
L10 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

L = np.array([L1, L2, L3, L4, L5, L6, L7, L8, L9, L10])
LR = np.array([L1R, L2, L3R, L4, L5, L6, L7, L8, L9, L10])

ITERATIONS = 100

def getM(L):
    M = np.zeros([10, 10], dtype=float)
    # number of outgoing links
    c = np.zeros([10], dtype=int)
    
    ## TODO 1 compute the stochastic matrix M
    for i in range(0, 10):
        c[i] = sum(L[i])
    
    for i in range(0, 10):
        for j in range(0, 10):
            if L[j][i] == 0: 
                M[i][j] = 0
            else:
                M[i][j] = 1.0/c[j]
    return M

def pagerank(M,pr,q,ones):
    pr += 1/10
    for i in range(ITERATIONS):
        pr = q*ones + (1-q)*np.matmul(M,pr)
    pr = pr/sum(pr)
    pr = sorted(enumerate(pr), key=lambda x: -x[-1])

    for i in pr:
        i = str(i).replace('(','').replace(')','').split(',')
        print(int(i[0])+1, ": ", i[1])

def trustrank(M,tr,q,d):
    for i in range(ITERATIONS):
        tr = q*d + (1-q)*np.matmul(M,tr)
    tr = tr/sum(tr)
    tr = sorted(enumerate(tr), key=lambda x: -x[-1])

    for i in tr:
        i = str(i).replace('(','').replace(')','').split(',')
        print(int(i[0])+1, ": ", i[1])

print("Matrix L (indices)")
print(L)    

M = getM(L)

print("Matrix M (stochastic matrix)")
print(M)

### TODO 2: compute pagerank with damping factor q = 0.15
### Then, sort and print: (page index (first index = 1 add +1) : pagerank)
### (use regular array + sort method + lambda function)
print("\nPAGERANK")

q = 0.15
pr = np.zeros([10], dtype=float)
ones = np.ones(10)
pagerank(M,pr,q,ones)

    
### TODO 3: compute trustrank with damping factor q = 0.15
### Documents that are good = 1, 2 (indexes = 0, 1)
### Then, sort and print: (page index (first index = 1, add +1) : trustrank)
### (use regular array + sort method + lambda function)
print("\nTRUSTRANK (DOCUMENTS 1 AND 2 ARE GOOD)")

q = 0.15

d = np.zeros([10], dtype=float)
d[0], d[1] = 1, 1
d = d/sum(d)
tr = [v for v in d]
trustrank(M,tr,q,d)
    
### TODO 4: Repeat TODO 3 but remove the connections 3->7 and 1->5 (indexes: 2->6, 0->4) 
### before computing trustrank
M2 = getM(LR)
print("\nTODO4 TRUSTRANK")
trustrank(M2,tr,q,d)