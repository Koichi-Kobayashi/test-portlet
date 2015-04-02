
package com.aimluck.eip.test;

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * TestのResultDataです。 <BR>
 *
 */
public class TestResultData implements ALData {

  /** Test ID */
  private ALNumberField test_id;


  /** タイトル */
  private ALStringField test_name;


  /** メモ */
  private ALStringField note;

  /** URL */
  private ALStringField url;


  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** ユーザID */
  private ALStringField user_id;


  /**
   * 期限状態（期限前/期限当日/期限後）． <br>
   * クラス TestUtils の変数 LIMIT_STATE_BEFORE，LIMIT_STATE_TODAY，LIMIT_STATE_AFTER
   * を参照．
   */
  private ALNumberField limit_state;

  private boolean is_self_test;

  private boolean hasAclEditTestOther;

  private boolean hasAclDeleteTestOther;

  /**
   *
   *
   */
  @Override
  public void initField() {
    test_id = new ALNumberField();
    test_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    create_date = new ALStringField();
    update_date = new ALDateTimeField();
    is_self_test = false;
    url = new ALStringField();  //４月２日追加
    url.setTrim(false);  //４月２日追加
  }

  /**
   * @return
   */
  public ALNumberField getTestId() {
    return test_id;
  }

  /**
   * @return
   */
  public String getTestName() {
    return ALCommonUtils.replaceToAutoCR(test_name.toString());
  }

  /**
   * @param i
   */
  public void setTestId(long i) {
    test_id.setValue(i);
  }

  /**
   * @param string
   */
  public void setTestName(String string) {
    test_name.setValue(string);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  /**
   * hasAclEditTestOtherを取得します。
   *
   * @return hasAclEditTestOther
   */
  public boolean hasAclEditTestOther() {
    return hasAclEditTestOther;
  }

  /**
   * hasAclEditTestOtherを設定します。
   *
   * @param hasAclEditTestOther
   *          hasAclEditTestOther
   */
  public void setAclEditTestOther(boolean hasAclEditTestOther) {
    this.hasAclEditTestOther = hasAclEditTestOther;
  }

  /**
   * hasAclDeleteTestOtherを取得します。
   *
   * @return hasAclDeleteTestOther
   */
  public boolean hasAclDeleteTestOther() {
    return hasAclDeleteTestOther;
  }

  /**
   * hasAclDeleteTestOtherを設定します。
   *
   * @param hasAclDeleteTestOther
   *          hasAclDeleteTestOther
   */
  public void setAclDeleteTestOther(boolean hasAclDeleteTestOther) {
    this.hasAclDeleteTestOther = hasAclDeleteTestOther;
  }

  //３月２７日作成
public void setUrlName(String compressString) {
	// TODO 自動生成されたメソッド・スタブ
	url.setValue(toString());
	}

public String getUrlName() {
    return ALCommonUtils.replaceToAutoCR(url.toString());
  }

 //４月２追加
public String getUrl() {
    return (url.getValue());
  }

public String getUrl2() {
	 return url.getValue();
  }

public String getUrlOnLink() {
    return ALEipUtils.getMessageList(url.getValue());
   }

   /**
   * @param string
   */
  public void setUrl(String string) {
    url.setValue(string);
  }

}
