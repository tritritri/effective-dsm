- To get the feeling of the code, you can run Exp4.java
you should see some debug printout,
the result is stored under directory Exp4-0.02

- The UCT tree, exploitation exploration score for selecting
the next child is in UCT.java

- The objective function for me is the simulation of the 
whole system. See Exp4.java line 111 

- Another important parameter is \theta (in the paper Eq. 7)
or C in my code Exp4.java line 52. This parameter is used 
to balance the exploration and exploitation.
Higher \theta (or C) encourages more exploration. 
Lower \theta encourages more exploitation.
About the magnitude of \theta -- this depends on the value
of your objective function. For example, in my case, the value 
of my objective function can be up to 850. Thus, 
I tried \theta=1, 10, 100, 500, and 1000.
-- you also need to try different \theta :)

- Finally sorry if the code is a little messy :)
