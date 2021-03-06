package controllerAdmin.product;

import beans.loginAdmin.UserAdmin;
import beans.productAdmin.ProductAdminEditSingle;
import beans.productAdmin.ProductAdminObject;
import beans.productAdmin.ProductAdminSizeAdd;
import model.personalNotice.PersonalNoticeModel;
import model.productDetailInformation.ProductDetailInformationModel;
import model.productImage.ProductImageModel;
import model.size.SizeModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ProductEditSingleController", urlPatterns = "/ProductEditSingleController")
public class ProductEditSingleController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        UserAdmin userAdmin = (UserAdmin) request.getSession().getAttribute("userAdmin");
        ProductAdminObject productAdminObject = (ProductAdminObject) userAdmin.getListOfFunction().get("productAdminObject");
        ProductAdminEditSingle productAdminEditSingle = productAdminObject.getProductAdminEditSingle();
        //--------------------------------------------------------

        String action = request.getParameter("action");
        String data = request.getParameter("data");

        if (action.equals("addSize")) {

            ProductAdminSizeAdd productAdminSizeAdd = SizeModel.getInstance().getProductAdminSizeAddById(data);
            productAdminEditSingle.getList_size().add(productAdminSizeAdd);

        } else if (action.equals("removeSize")) {
            for (ProductAdminSizeAdd productAdminSizeAdd : productAdminEditSingle.getList_size()) {
                if(productAdminSizeAdd.getId().equals(data)){
                    productAdminEditSingle.getList_size().remove(productAdminSizeAdd);
                    break;
                }
            }
        }else if(action.equals("removeImg")){
            productAdminEditSingle.getList_hinh_anh_sp().remove(data);
        }else if(action.equals("addImage")){
            productAdminEditSingle.getList_hinh_anh_sp().add(data);
            productAdminEditSingle.setHinh_anh_trong_firebase(productAdminEditSingle.getHinh_anh_trong_firebase()+1);
        }


        if(action.equals("save")){

            productAdminObject.setNotify(true);
            productAdminObject.setTitle("");
            productAdminObject.setContent("D??? li???u c???a b???n ???? ???????c thay ?????i");

            //  T???o th??ng b??o c?? nh??n
            PersonalNoticeModel.getInstance().addNewPersonalNoticeToDatabase(userAdmin.getAccount().getId(), "B???n", "v???a s???a m???t", "s???n ph???m" ,"c?? th??ng tin l??: ","M?? m??u: #"+productAdminEditSingle.getMa_sp()+", T??n m??u: "+productAdminEditSingle.getMa_mau());

            //  C???p nh???t trong hinh_anh_sp
           ProductImageModel.getInstance().updateImgProductSingleEdit(productAdminEditSingle.getMa_sp(),productAdminEditSingle.getMa_mau(),productAdminEditSingle.getList_hinh_anh_sp());

            // C???p nh???t trong trang chi ti???t s???n ph???m
           ProductDetailInformationModel.getInstance().updateEditSingle(productAdminEditSingle.getMa_sp(),productAdminEditSingle.getMa_mau(),productAdminEditSingle.getList_size(),productAdminEditSingle.getHinh_anh_trong_firebase());

            request.setAttribute("forward", "editSingle");
            request.setAttribute("more", "C???p nh???t d??? li???u th??nh c??ng s???n ph???m : M?? s???n ph???m: #" + productAdminEditSingle.getMa_sp()+", T??n m??u: "+productAdminEditSingle.getTen_mau());
            request.setAttribute("more2", "C?? s??? d??? li???u ???? ???????c thay ?????i");

            request.getRequestDispatcher("ProductController").forward(request, response);

        }else{

        //----------------------------------------------------------
        productAdminObject.setReady(true);
        productAdminObject.setProductAdminEditSingle(productAdminEditSingle);
        userAdmin.getListOfFunction().put("productAdminObject", productAdminObject);
        userAdmin.updateReady("productAdminObject");
        request.getSession().setAttribute("userAdmin", userAdmin);

        // sedirect t???i trang c???a m??nh th??i n??o
        response.sendRedirect("admin/home/quanLySanPham.jsp");}
    }
}
