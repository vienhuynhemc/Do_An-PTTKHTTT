package controllerAdmin.quanLyDonHang;

import beans.loginAdmin.UserAdmin;
import beans.nextPage.NextPageObject;
import beans.quanLyDonHang.QuanLyDonHang;
import beans.quanLyDonHang.QuanLyDonHangObjectNVGH;
import model.nextPage.NextPageModel;
import model.quanLyDonHangModel.QuanLyDonHangModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "QuanLyDonHangControllerNVGH", urlPatterns = "/QuanLyDonHangControllerNVGH")
public class QuanLyDonHangControllerNVGH extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
        //todo doPost
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserAdmin userAdmin = (UserAdmin) request.getSession().getAttribute("userAdmin");
        if (userAdmin == null) {
            request.getSession().setAttribute("trackPage", "admin.quanLyDonHangNVGH");
            response.sendRedirect("admin/home/login.jsp");
        } else {
            //  Kiểm tra foward, xem trang khác có foward tới này không
            String foward = (String) request.getAttribute("forward");

            //  Nếu khác null thì xem thử nó là từ trang nào
            if (foward != null) {
                //  Lấy đối tượng ra
                QuanLyDonHangObjectNVGH QuanLyDonHangObjectNVGH = (QuanLyDonHangObjectNVGH) userAdmin.getListOfFunction().get("QuanLyDonHangObjectNVGH");

                List<QuanLyDonHang> list_quan_ly_don_hang = QuanLyDonHangModel.getInstance().layDanhDanhAllNVGH(QuanLyDonHangObjectNVGH.getSelectSearchAndSort(), QuanLyDonHangObjectNVGH.getSearch(), QuanLyDonHangObjectNVGH.getSort(), QuanLyDonHangObjectNVGH.getNowPage(), userAdmin.getAccount().getId());
                QuanLyDonHangObjectNVGH.setList_don_hang(list_quan_ly_don_hang);

                if (foward.equals("accept")) {

                    //  Cập nhập lại số lượng hiển thị
                    QuanLyDonHangObjectNVGH.setNumberOfShow(list_quan_ly_don_hang.size());

                    //  Cập nhập lại số sản phẩm tối đa
                    int maximumEmailNotification = QuanLyDonHangModel.getInstance().layDanhSachToiDaDauTienNVGH(userAdmin.getAccount().getId());
                    QuanLyDonHangObjectNVGH.setMaximumEmailNotification(maximumEmailNotification);

                    //  Cập nhập lại số trang tối đa
                    int maximumPage = QuanLyDonHangModel.getInstance().getMaximunNumberOfPage(maximumEmailNotification);
                    QuanLyDonHangObjectNVGH.setMaximumPage(maximumPage);

                    //  Cập nhập lại list Next page
                    List<NextPageObject> nextPages = NextPageModel.getInstance().getListNextPageObjectAdmin(QuanLyDonHangObjectNVGH.getNowPage(), QuanLyDonHangObjectNVGH.getMaximumPage());
                    QuanLyDonHangObjectNVGH.setNextPages(nextPages);

                    //  Cập nhập lại là hãy thông báo
                    QuanLyDonHangObjectNVGH.setNotify(true);
                    QuanLyDonHangObjectNVGH.setTitle((String) request.getAttribute("more"));
                    QuanLyDonHangObjectNVGH.setConntent((String) request.getAttribute("more2"));

                }

                //  Gán lại cho sesstion
                QuanLyDonHangObjectNVGH.setReady(true);
                userAdmin.getListOfFunction().put("QuanLyDonHangObjectNVGH", QuanLyDonHangObjectNVGH);
                userAdmin.updateReady("QuanLyDonHangObjectNVGH");
                request.getSession().setAttribute("userAdmin", userAdmin);


                // sedirect tới trang của mình thôi nào
                response.sendRedirect("admin/home/quanLyDonHangNVGH.jsp");
            } else {

                //  Không thì chạy bình thường như chưa từng xảy ra

                // set charset cho cả request và responne
                request.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                response.setCharacterEncoding("UTF-8");

                //----------Lấy những thông tin cần thiết của trang này------------------//

                //  Select sort and search
                String selectSearchAndSort = request.getParameter("selectSearchAndSort");

                //  sort DESC hay ASC, null == DESC on == ASC
                String sort = request.getParameter("sort");

                //  Nội dung ô tìm kiếm
                String search = request.getParameter("search");

                //  Số trang đi tới
                String numberOfPage = request.getParameter("numberOfPage");

                //  Số trang maximun
                String maximunNumberOfPage = request.getParameter("maximunNumberOfPage");

                //  action
                String action = request.getParameter("action");

                //--------------------------------------------------------------------------

                //  Kiểm tra thử select search and sort có dữ liệu hay chưa
                if (selectSearchAndSort == null) {

                    //  Nếu chưa thì đây là lần đầu tiên tới trang này
                    //  Trước tiên lấy email thông báo đầu tiên
                    List<QuanLyDonHang> list_qldh = QuanLyDonHangModel.getInstance().layDanhSachDauTienNVGH(userAdmin.getAccount().getId());

                    //  Số trang hiện tại chắc chắn là 1
                    int nowPage = 1;

                    //  Số hãng sản xuất tốt đa
                    int maximumEmailNotification = QuanLyDonHangModel.getInstance().layDanhSachToiDaDauTienNVGH(userAdmin.getAccount().getId());

                    //  Số trang tối đa
                    int maximunPage = QuanLyDonHangModel.getInstance().getMaximunNumberOfPage(maximumEmailNotification);

                    //  Lấy list nextPage
                    List<NextPageObject> nextPages = NextPageModel.getInstance().getListNextPageObjectAdmin(nowPage, maximunPage);

                    //  Khi mới chuyển tới trang này thì tạo 1 EmailNotifictaioNobject
                    QuanLyDonHangObjectNVGH QuanLyDonHangObjectNVGH = new QuanLyDonHangObjectNVGH();
                    QuanLyDonHangObjectNVGH.setList_don_hang(list_qldh);
                    QuanLyDonHangObjectNVGH.setNowPage(nowPage);
                    QuanLyDonHangObjectNVGH.setMaximumEmailNotification(maximumEmailNotification);
                    QuanLyDonHangObjectNVGH.setMaximumPage(maximunPage);
                    QuanLyDonHangObjectNVGH.setNumberOfShow(list_qldh.size());
                    QuanLyDonHangObjectNVGH.setNextPages(nextPages);
                    QuanLyDonHangObjectNVGH.setSort("DESC");
                    QuanLyDonHangObjectNVGH.setSearch("");
                    QuanLyDonHangObjectNVGH.setSelectSearchAndSort("ngay_tao");

                    QuanLyDonHangObjectNVGH.setReady(true);
                    userAdmin.getListOfFunction().put("QuanLyDonHangObjectNVGH", QuanLyDonHangObjectNVGH);
                    userAdmin.updateReady("QuanLyDonHangObjectNVGH");
                    request.getSession().setAttribute("userAdmin", userAdmin);

                    System.out.println(QuanLyDonHangObjectNVGH.getList_don_hang().size());

                    // sedirect tới trang của mình thôi nào
                    response.sendRedirect("admin/home/quanLyDonHangNVGH.jsp");

                } else {
                    //  Action load là một thứ gì đó khác hoàn toàn nên ta phải làm như trường hợp xóa
                    if (action.equals("load")) {

                        //  Lấy đối tượng ra
                        QuanLyDonHangObjectNVGH emailNotificationObject = (QuanLyDonHangObjectNVGH) userAdmin.getListOfFunction().get("QuanLyDonHangObjectNVGH ");

                        //  Lấy lại list emailNotification đổ dữ liệu
                        List<QuanLyDonHang> emailNotificationList = QuanLyDonHangModel.getInstance().layDanhDanhAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), emailNotificationObject.getSort(), emailNotificationObject.getNowPage(), userAdmin.getAccount().getId());

                        //  Cập nhập lại list emailNotification
                        emailNotificationObject.setList_don_hang(emailNotificationList);

                        //  Kiểm tra nếu nhưemailNotificationList.size == 0 thì có nghĩa trang này hết dữ liệu rồi, cập nhập lại nowPage -1
                        if (emailNotificationList.size() == 0) {
                            if (emailNotificationObject.getNowPage() > 0) {
                                emailNotificationObject.setNowPage(emailNotificationObject.getNowPage() - 1);
                                emailNotificationList = QuanLyDonHangModel.getInstance().layDanhDanhAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), emailNotificationObject.getSort(), emailNotificationObject.getNowPage(), userAdmin.getAccount().getId());
                                emailNotificationObject.setList_don_hang(emailNotificationList);
                            }
                        }

                        //  Cập nhập lại số lượng hiển thị
                        emailNotificationObject.setNumberOfShow(emailNotificationList.size());

                        //  Cập nhập lại số sản phẩm tối đa
                        int maximumEmailNotification = QuanLyDonHangModel.getInstance().layDanhSachToiDaFromAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), userAdmin.getAccount().getId());
                        emailNotificationObject.setMaximumEmailNotification(maximumEmailNotification);

                        //  Cập nhập lại số trang tối đa
                        int maximumPage = QuanLyDonHangModel.getInstance().getMaximunNumberOfPage(maximumEmailNotification);
                        emailNotificationObject.setMaximumPage(maximumPage);

                        //  Cập nhập lại list Next page
                        List<NextPageObject> nextPages = NextPageModel.getInstance().getListNextPageObjectAdmin(emailNotificationObject.getNowPage(), emailNotificationObject.getMaximumPage());
                        emailNotificationObject.setNextPages(nextPages);

                        //  Gán lại cho sesstion
                        emailNotificationObject.setReady(true);
                        userAdmin.getListOfFunction().put("QuanLyDonHangObjectNVGH", emailNotificationObject);
                        userAdmin.updateReady("QuanLyDonHangObjectNVGH");
                        request.getSession().setAttribute("userAdmin", userAdmin);

                        // sedirect tới trang của mình thôi nào
                        response.sendRedirect("admin/home/quanLyDonHangNVGH.jsp");

                    } else {

                        //  Lấy đối tượng ra
                        QuanLyDonHangObjectNVGH emailNotificationObject = (QuanLyDonHangObjectNVGH) userAdmin.getListOfFunction().get("QuanLyDonHangObjectNVGH ");

                        switch (action) {

                            case "sort":

                                //  Lấy lại cách sắp xếp
                                if (sort != null) {
                                    emailNotificationObject.setSort("DESC");
                                } else {
                                    emailNotificationObject.setSort("ASC");
                                }

                                break;

                            case "changeFilter":

                                //  Lấy lại bộ lọc
                                emailNotificationObject.setSelectSearchAndSort(selectSearchAndSort);

                                //  Cập nhập lại maximun hãng sản xuất
                                int maximumEmailNotification = QuanLyDonHangModel.getInstance().layDanhSachToiDaFromAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), userAdmin.getAccount().getId());
                                emailNotificationObject.setMaximumEmailNotification(maximumEmailNotification);

                                //  Cập nhập lại số trang tối đa
                                int maximumPagee = QuanLyDonHangModel.getInstance().getMaximunNumberOfPage(maximumEmailNotification);
                                emailNotificationObject.setMaximumPage(maximumPagee);

                                //  Cập nhập lại list nextPage
                                List<NextPageObject> nextPagesss = NextPageModel.getInstance().getListNextPageObjectAdmin(1, maximumPagee);
                                emailNotificationObject.setNextPages(nextPagesss);

                                //  Cập nhập lại nowPage là 1
                                emailNotificationObject.setNowPage(1);

                                break;

                            case "nextPage":

                                //  Lấy lại nowPage
                                emailNotificationObject.setNowPage(Integer.parseInt(numberOfPage));

                                //  Cập nhập lại list nextPage
                                List<NextPageObject> nextPages = NextPageModel.getInstance().getListNextPageObjectAdmin(emailNotificationObject.getNowPage(), emailNotificationObject.getMaximumPage());
                                emailNotificationObject.setNextPages(nextPages);
                                break;

                            case "search":

                                //  Cập nhập now page là 1
                                emailNotificationObject.setNowPage(1);

                                //  Lấy lại search
                                emailNotificationObject.setSearch(search);

                                //  Cập nhập lại maximun hãng sản xuất
                                maximumEmailNotification = QuanLyDonHangModel.getInstance().layDanhSachToiDaFromAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), userAdmin.getAccount().getId());
                                emailNotificationObject.setMaximumEmailNotification(maximumEmailNotification);

                                //  Cập nhập lại số trang tối đa
                                int maximumPage = QuanLyDonHangModel.getInstance().getMaximunNumberOfPage(maximumEmailNotification);
                                emailNotificationObject.setMaximumPage(maximumPage);

                                //  Cập nhập lại list nextPage
                                List<NextPageObject> nextPagess = NextPageModel.getInstance().getListNextPageObjectAdmin(1, maximumPage);
                                emailNotificationObject.setNextPages(nextPagess);

                                break;

                        }

                        //  Lấy lại hãng sản xuất
                        List<QuanLyDonHang> emailNotificationList = QuanLyDonHangModel.getInstance().layDanhDanhAllNVGH(emailNotificationObject.getSelectSearchAndSort(), emailNotificationObject.getSearch(), emailNotificationObject.getSort(), emailNotificationObject.getNowPage(), userAdmin.getAccount().getId());
                        emailNotificationObject.setList_don_hang(emailNotificationList);

                        //  Cập nhập lại số sản phẩm hiện thị
                        emailNotificationObject.setNumberOfShow(emailNotificationList.size());


                        //  Gán lại cho sesstion
                        emailNotificationObject.setReady(true);
                        userAdmin.getListOfFunction().put("QuanLyDonHangObjectNVGH", emailNotificationObject);
                        userAdmin.updateReady("QuanLyDonHangObjectNVGH");
                        request.getSession().setAttribute("userAdmin", userAdmin);

                        // sedirect tới trang của mình thôi nào
                        response.sendRedirect("admin/home/quanLyDonHangNVGH.jsp");
                    }
                }
            }
        }
    }
}
