/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */

import java.util.Map;
import java.util.HashMap;
import org.apache.thrift.TEnum;

public enum Status implements org.apache.thrift.TEnum {
  FAILED(0),
  SUCCESSFUL(1),
  VOTE_REQUEST(2),
  VOTE_COMMIT(3),
  VOTE_ABORT(4),
  GLOBAL_COMMIT(5),
  GLOBAL_ABORT(6),
  COMMIT(7),
  ABORT(8);

  private final int value;

  private Status(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static Status findByValue(int value) { 
    switch (value) {
      case 0:
        return FAILED;
      case 1:
        return SUCCESSFUL;
      case 2:
        return VOTE_REQUEST;
      case 3:
        return VOTE_COMMIT;
      case 4:
        return VOTE_ABORT;
      case 5:
        return GLOBAL_COMMIT;
      case 6:
        return GLOBAL_ABORT;
      case 7:
        return COMMIT;
      case 8:
        return ABORT;
      default:
        return null;
    }
  }
}