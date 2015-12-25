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