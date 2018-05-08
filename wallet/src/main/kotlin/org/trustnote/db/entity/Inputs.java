package org.trustnote.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.Integer;
import java.lang.String;

@Entity(tableName = "inputs", primaryKeys = {"unit", "input_index"})
public class Inputs extends TBaseEntity {
  @ColumnInfo(
      name = "unit"
  )
  @NonNull
  @SerializedName("owner_unit")
  public String unit;

  @ColumnInfo(
      name = "message_index"
  )
  @NonNull
  @SerializedName("msg_non_dup_index")
  public int messageIndex;

  @ColumnInfo(
      name = "input_index"
  )
  @NonNull
  public int inputIndex;

  @ColumnInfo(
      name = "asset"
  )
  public String asset;

  @ColumnInfo(
      name = "denomination"
  )
  public int denomination;

  @ColumnInfo(
      name = "is_unique"
  )
  public Integer isUnique;

  @ColumnInfo(
      name = "type"
  )
  public String type;

  @ColumnInfo(
      name = "src_unit"
  )
  @SerializedName("unit")
  public String srcUnit;

  @ColumnInfo(
      name = "src_message_index"
  )
  @SerializedName("message_index")
  public Integer srcMessageIndex;

  @ColumnInfo(
      name = "src_output_index"
  )
  @SerializedName("output_index")
  public Integer srcOutputIndex;

  @ColumnInfo(
      name = "from_main_chain_index"
  )
  public Integer fromMainChainIndex;

  @ColumnInfo(
      name = "to_main_chain_index"
  )
  public Integer toMainChainIndex;

  @ColumnInfo(
      name = "serial_number"
  )
  public Integer serialNumber;

  @ColumnInfo(
      name = "amount"
  )
  public Long amount;

  @ColumnInfo(
      name = "address"
  )
  public String address;

  @Ignore
  public MyAddresses srcAddress;
}
