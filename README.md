► Created a Remote File Service that supports Two Phase Commit Protocol across replicated file  servers using Apache Thrift. The project consisted of 3 parts :-

►  Durable Remote File Service
      Implemented a durable remote file service that supported the functions: Read File, Write File, Delete File
      
► Two Phase Commit

     It consists of single “coordinator” process and multiple “participant” processes: 
     
     ➢ Coordinator - Exposes RPC interface to the clients and coordinates and dispatches operations to    the replicated servers.
     
     ➢ Participants - Exposes RPC interface to the Coordinator and process multiple commands concurrently from coordinator.
     
     ➢ Used SQLite for logging to keep durable state associated with the two-phase commit. 
     
► Client
     Multiple clients connect to the coordinator and issue file operations to the Coordinator concurrently.
     

TO COMPILE:
[
  make
]

TO RUN:
[
  ./participant.sh p1 9091
  ./participant.sh p2 9093
  ./coordinator.sh branches.txt 9094
  ./client.sh 127.0.0.1 9094 abc.txt delete
]
