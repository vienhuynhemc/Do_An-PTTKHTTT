package controllerAdmin.quanLyDonHang;

import beans.DateTime;
import beans.DateTimeConfiguration;
import beans.loginAdmin.UserAdmin;
import connectionDatabase.DataSource;
import model.personalNotice.PersonalNoticeModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "QuanLyDonHangDuyetAdminNVK", urlPatterns = "/QuanLyDonHangDuyetAdminNVK")
public class QuanLyDonHangDuyetAdminNVK extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        String idDh = request.getParameter("ma_dh");
        String idNv =request.getParameter("ma_nv");
        DateTime nowDate = new DateTime(DateTimeConfiguration.NOW_DATE);

        UserAdmin userAdmin = (UserAdmin) request.getSession().getAttribute("userAdmin");
        Connection connection = DataSource.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE don_hang SET nvk_dong_goi =? ,trang_thai_van_chuyen= ?,ma_nvgh = ? WHERE ma_don_hang =?");
            preparedStatement.setString(1, nowDate.toString());
            preparedStatement.setInt(2, 2);
            preparedStatement.setString(3,idNv);
            preparedStatement.setString(4, idDh);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);

        request.setAttribute("more", "Duy???t ????n h??ng th??nh c??ng cho nh??n vi??n giao h??ng");
        request.setAttribute("more2", "D??? li???u kh??ng c?? g?? thay ?????i");

        //  T???o th??ng b??o c?? nh??n
        PersonalNoticeModel.getInstance().addNewPersonalNoticeToDatabase(userAdmin.getAccount().getId(), "B???n", "v???a duy???t", "m???t ????n h??ng cho Nh??n vi??n giao h??ng" ,"v???i th??ng tin l??:", "#ID_Nh??n vi??n: "+idNv+", #ID_????n h??ng: "+idDh);

        //  Xong foward t???i controller ????? d??? li???u
        request.getRequestDispatcher("QuanLyDonHangControllerNVK").forward(request, response);
    }
}
