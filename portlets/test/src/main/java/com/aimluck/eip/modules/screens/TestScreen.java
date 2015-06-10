/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.modules.screens;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.test.TestSelectData;
import com.aimluck.eip.test.util.TestUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Testの一覧を処理するクラスです。 <br />
 *
 */
public class TestScreen extends ALVelocityScreen {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TestScreen.class.getName());

  /**
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  @Override
  protected void doOutput(RunData rundata, Context context) throws Exception {

    VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {

//      if ("update".equals(mode)) {
//        updateState(rundata, context, portlet);
//      }
      ParameterParser parser = rundata.getParameters();

      TestUtils.passPSML(rundata, context, "p12f-filters", parser
        .getString(ALEipConstants.LIST_FILTER));
      TestUtils.passPSML(rundata, context, "p12g-filtertypes", parser
        .getString(ALEipConstants.LIST_FILTER_TYPE));

      // 選択しているタブ情報の削除
      ALEipUtils.removeTemp(rundata, context, "tab");

      TestSelectData listData = new TestSelectData();
      listData.initField();
//      listData.loadCategoryList(rundata);
      listData.setRowsNum(Integer.parseInt(portlet
        .getPortletConfig()
        .getInitParameter("p1a-rows")));
//      listData.setTableColumNum(Integer.parseInt(portlet
//        .getPortletConfig()
//        .getInitParameter("p0e-rows")));
      listData.setStrLength(0);
      listData.doViewList(this, rundata, context);

      String layout_template = "portlets/html/ajax-test.vm";
      setTemplate(rundata, context, layout_template);

    } catch (Exception ex) {
      logger.error("[TestScreen] Exception.", ex);
      ALEipUtils.redirectDBError(rundata);
    }
  }

//  protected void updateState(RunData rundata, Context context,
//      VelocityPortlet portlet) {
//    TestStateUpdateData data = new TestStateUpdateData();
//    data.initField();
//    data.doUpdate(this, rundata, context);
//  }

  /**
   * @return
   */
  @Override
  protected String getPortletName() {
    return TestUtils.TEST_PORTLET_NAME;
  }

}
