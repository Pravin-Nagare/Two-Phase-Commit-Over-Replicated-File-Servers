typedef string UserID
typedef i64 Timestamp

exception SystemException {
  1: optional string message
}


enum Status {
  FAILED = 0;
  SUCCESSFUL = 1;
  VOTE_REQUEST = 2;
  VOTE_COMMIT = 3;
  VOTE_ABORT = 4;
  GLOBAL_COMMIT = 5;
  GLOBAL_ABORT = 6;
  COMMIT = 7;
  ABORT = 8;
}

struct ParticipantID {
  1:string name;
  2:string ip;
  3:i32 port;
  4:optional Status status;
}

struct RFile {
  1: optional string filename;
  2: optional string content;
  3: optional Status status;
}

struct Transaction {
  1:list<ParticipantID> participantID;
  2:i32 tid;
  3:optional Status status;
  4:RFile rFile;
  5:string operation;
}

struct StatusReport {
  1: required Status status;
}

service FileStore {
  StatusReport writeFile(1: RFile rFile)
    throws (1: SystemException systemException),
  
  RFile readFile(1: string filename)
    throws (1: SystemException systemException),
    
  StatusReport deleteFile(1: RFile rFile)
    throws (1: SystemException systemException),
  
  StatusReport canCommit(1: Transaction trans)
  	throws (1: SystemException systemException),
  	
  StatusReport doCommit(1: Transaction trans)
  	throws (1: SystemException systemException),
  	
  void doAbort(1: Transaction trans)
  	throws (1: SystemException systemException),
}