# wRebeca

wRebca is a tool for modeling and verification of protocols in a static or dynamic network. We refere to a network with more than one possible topology as the dynaimc network. For example, mobile Ad Hoc Networks (MANETs) have dynamic network since their topology is constantly changing. 

wRebeca model is an actor-based modeling language in which different components of a system/protocol are modeled by  indipendent and concurrent actors which can communicate only through messgae passing. Each actor has its own memory to store its state variables and a queue/bag to store its recieved messages. It also has some message servers for processing the recieved messgaes. 


wRebeca tool provide some reduction techniques to overcome state space explosion which is a well-known problem in model checking approaches. 
