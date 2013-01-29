package com.renren.dp.xlog.web.action;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sso.api.inter.SSOImpl;
import com.sso.api.inter.SSOInterface;

public class DispatcherController extends HttpServlet {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private static final String forward = "http://dap.d.xiaonei.com";

  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    RequestDispatcher dispatcher = null;
    String operator = req.getParameter("operator");
    if (operator.equals("logout")) {
      HttpSession session = req.getSession();
      session.removeAttribute("username");
      session.invalidate();
      session = null;
      SSOInterface sso = new SSOImpl();
      sso.ppClearLocalLogin(req, res);
      sso.ppLogout(req, res, forward);
      return;
    }
   dispatcher = req.getRequestDispatcher("/index.jsp");
    
   dispatcher.forward(req, res);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doPost(req, res);
  }
}
