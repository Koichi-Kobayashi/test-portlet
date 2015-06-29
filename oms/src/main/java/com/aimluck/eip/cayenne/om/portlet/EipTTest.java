package com.aimluck.eip.cayenne.om.portlet;

import org.apache.cayenne.ObjectId;

import com.aimluck.eip.cayenne.om.portlet.auto._EipTTest;

public class EipTTest extends _EipTTest {

  public static final String TEST_NAME_COLUMN = "TEST_NAME";

  public Integer getTestId() {

    if (getObjectId() != null && !getObjectId().isTemporary()) {

      Object obj = getObjectId().getIdSnapshot().get(TEST_ID_PK_COLUMN);

      if (obj instanceof Long) {

        Long value = (Long) obj;

        return Integer.valueOf(value.intValue());

      } else {

        return (Integer) obj;

      }

    } else {

      return null;

    }

  }

  public void setTestId(String id) {

    setObjectId(new ObjectId("EipTTest", TEST_ID_PK_COLUMN, Integer.valueOf(id)));

  }

public String getTestURL() {
	// TODO 自動生成されたメソッド・スタブ
	return null;
}

}
