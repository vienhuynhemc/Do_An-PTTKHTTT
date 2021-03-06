package worksWithDatabase.category;

import beans.DateTime;
import beans.category.Category;
import beans.productAdmin.ProductAdmin;
import beans.productAdmin.ProductAdminAdd;
import beans.productAdmin.ProductAdminCategory;
import connectionDatabase.DataSource;
import model.category.CategoryModel;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class CategoryWorksWithDatabase {
    ArrayList<Category> list;
    static int numberOfPage;
    static int numberCategories;

    public ArrayList<Category> LoadAllCategories(int page, String type, String search, String orderBy,int numberPerPage){
        Connection connection = DataSource.getInstance().getConnection();
        try{
            String sql = "SELECT * from danh_muc where (ma_dm like ? or ten_dm like ? or DAY(ngay_tao) = ?" +
                    "or MONTH(ngay_tao) = ? or Year(ngay_tao) = ?) " +
                    " AND ton_tai = 1 ORDER BY "+type+ " "+ orderBy +" LIMIT ?," +numberPerPage;
            String sql1 = "SELECT * from danh_muc where (ma_dm like ? or ten_dm like ? or DAY(ngay_tao) = ?" +
                    "or MONTH(ngay_tao) = ? or Year(ngay_tao) = ?) AND ton_tai = 1";

            PreparedStatement s = connection.prepareStatement(sql1);
            s.setString(1,"%"+search+"%");
            s.setString(2,"%"+search+"%");
            s.setString(3,search);
            s.setString(4,search);
            s.setString(5,search);

            ResultSet rs = s.executeQuery();
            rs.last();
            numberCategories = rs.getRow();
            rs.beforeFirst();

            if(numberCategories%numberPerPage == 0){
                setNumberOfPage(numberCategories/numberPerPage);
            }
            else{
                setNumberOfPage((numberCategories/numberPerPage) + 1);
            }
            rs.close();
            s.close();

            PreparedStatement s2 = connection.prepareStatement(sql);
            int start = (page - 1) * numberPerPage + 1;
            s2.setString(1,"%"+search+"%");
            s2.setString(2,"%"+search+"%");
            s2.setString(3,search);
            s2.setString(4,search);
            s2.setString(5,search);
            s2.setInt(6,start-1);


            ResultSet rss = s2.executeQuery();
            list = new ArrayList<Category>();

            while(rss.next()){

                // l???y ra ng??y ????? s??? l?? r cho n??o class datetime
                String date = rss.getString("ngay_tao");
                String[] split = date.split(" ");

                String[] dmy = split[0].split("-");
                String[] time = split[1].split(":");

                int year = Integer.parseInt(dmy[0]);
                int month = Integer.parseInt(dmy[1]);
                int day = Integer.parseInt(dmy[2]);
                int hour = Integer.parseInt(time[0]);
                int minute = Integer.parseInt(time[1]);

                double d = Double.parseDouble(time[2]);

                int second = (int) d;

                DateTime datetime = new DateTime(year, month, day, hour, minute, second);


                Category c = new Category();
                String id = rss.getString("ma_dm");
                c.setId(id);
                c.setName(rss.getString("ten_dm"));
                c.setDateCreated(datetime);
                c.setExist(rss.getInt("ton_tai"));
                c.setNumberOfProduct(getProductNumberById(id));
                list.add(c);

            }
            rss.close();
            s2.close();
            DataSource.getInstance().releaseConnection(connection);
            return list;
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();

        }
        DataSource.getInstance().releaseConnection(connection);
        return list;
    }
    public int getProductNumberById(String id){
        Connection connection = DataSource.getInstance().getConnection();
        try{
            String sql = "SELECT COUNT(*) FROM san_pham s, danh_muc d WHERE d.ma_dm = s.ma_dm AND d.ma_dm = ?";
            PreparedStatement s = connection.prepareStatement(sql);
            s.setString(1,id);
            ResultSet rs = s.executeQuery();
            int a = 0;
            if(rs.next()){
                a = rs.getInt(1);
            }
            rs.close();
            s.close();
            DataSource.getInstance().releaseConnection(connection);
            return a;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);
        return 0;
    }

//    public static void main(String[] args) throws SQLException{
//        LoadCategoryDAO lao = new LoadCategoryDAO();
//
//        ArrayList<Category> list2 = lao.LoadAllCategories(1,"ten_dm","","ASC",3);
//
//        for(Category c : list2){
//            System.out.println(c);
//        }
////        System.out.println(list2.toString());
//        System.out.println(lao.getNumberOfPage());
//        System.out.println(list2.size());
//    }

    public ArrayList<Category> getList() {
        return list;
    }

    public void setList(ArrayList<Category> list) {
        this.list = list;
    }

    public static int getNumberOfPage() {
        return numberOfPage;
    }

    public static void setNumberOfPage(int numberOfPage) {
        CategoryWorksWithDatabase.numberOfPage = numberOfPage;
    }

    public static int getNumberCategories() {
        return numberCategories;
    }

    public static void setNumberCategories(int numberCategories) {
        CategoryWorksWithDatabase.numberCategories = numberCategories;
    }

    public CategoryWorksWithDatabase() {
    }
    // ki???m tra danh m???c ???? c?? t???n t???i trong h??? th???ng ch??a
    public boolean check(String id) {

        Connection c = DataSource.getInstance().getConnection();

        try {
            PreparedStatement p = c.prepareStatement("SELECT * FROM danh_muc WHERE ma_dm = ? AND ton_tai = 1");
            p.setString(1, id);
            ResultSet rs = p.executeQuery();
            int count = 0;
            if (rs.next()) {
               count++;
            }
                if(count > 0){
                    DataSource.getInstance().releaseConnection(c);
                    return true;
            }
            rs.close();
            p.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DataSource.getInstance().releaseConnection(c);
        return false;
    }
    public boolean checkName(String name) {

        Connection c = DataSource.getInstance().getConnection();

        try {
            PreparedStatement p = c.prepareStatement("SELECT * FROM danh_muc WHERE ten_dm = ? AND ton_tai = 1");
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            int count = 0;
            if (rs.next()) {
                count++;
            }
            if(count > 0){
                DataSource.getInstance().releaseConnection(c);
                rs.close();
                p.close();
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DataSource.getInstance().releaseConnection(c);
        return false;
    }

    // th??m m???t danh m???c m???i
    public boolean addCategory(String name){
        // l???y ra 1 connection
        Connection connection = DataSource.getInstance().getConnection();
        try {
                PreparedStatement check = connection.prepareStatement("SELECT * FROM danh_muc where ten_dm = ?");
                check.setString(1,name);
                ResultSet rsc = check.executeQuery();
                if(rsc.next()){
                    System.out.println("Ten danh muc da ton tai!");
                    DataSource.getInstance().releaseConnection(connection);
                    rsc.close();
                    check.close();
                    return false;
                }
                else {
                    Statement statement = connection.createStatement();
                    ResultSet rs = statement.executeQuery("SELECT * FROM danh_muc");
                    rs.beforeFirst();
                    rs.last();
                    int row = rs.getRow();
                    rs.close();

                    LocalDate date = LocalDate.now();
                    LocalDateTime now = LocalDateTime.now();
                    DateTime dateTime = new DateTime(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
                    PreparedStatement s = connection.prepareStatement("INSERT INTO danh_muc VALUES (?,?,?,?)");
                    s.setString(1, "dm_" + (row + 1));
                    s.setString(2, name);
                    s.setString(3, dateTime.toString());
                    s.setInt(4, 1);

                    int a = s.executeUpdate();

                    if (a > 0) {
                        DataSource.getInstance().releaseConnection(connection);
                        System.out.println("Them danh muc thanh cong!");
                        return true;
                    }
                    s.close();
                }

        }catch (Exception e){
            e.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);
        System.out.println("Kh??ng th??? th??m danh m???c do ???? t???n t???i");
        return false;
    }
    // x??a m???t danh m???c
    public boolean removeCategory(String id){
        // l???y ra 1 connection
        Connection connection = DataSource.getInstance().getConnection();
        try {
                    if(!check(id)){
                        DataSource.getInstance().releaseConnection(connection);
                        System.out.println("Khong ton tai danh muc!");
                        return false;
                    }
                    else {
                        PreparedStatement s = connection.prepareStatement("UPDATE danh_muc SET ton_tai = 0 where ma_dm = ?");
                        s.setString(1, id);
                        int a = s.executeUpdate();
                        s.close();
                        if (a > 0) {
                            DataSource.getInstance().releaseConnection(connection);
                            System.out.println("Xoa thanh cong");
                            return true;
                        }
                    }


        }catch (Exception e){
            e.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);
        System.out.println("Kh??ng th??? x??a danh m???c!");
        return false;
    }
    // c???p nh???t danh m???c s???n ph???m
    public boolean updateCategory(String id, String newName){
        Connection connection = DataSource.getInstance().getConnection();
        try {
                PreparedStatement s = connection.prepareStatement("UPDATE danh_muc SET ten_dm = ? WHERE ma_dm = ? AND ton_tai = 1");
                s.setString(1, newName);
                s.setString(2, id);
                int a = s.executeUpdate();
                if(a > 0) {
                    DataSource.getInstance().releaseConnection(connection);
                    System.out.println("Cap nhat thanh cong!");
                    return true;
                }
                s.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);
        System.out.println("Khong the cap nhat danh muc!");
        return false;
    }
    // hi???n th??? danh s??ch danh m???c s???n ph???m
    public static ArrayList<Category> getAllCategories(){
        ArrayList<Category> categories = new ArrayList<>();
        Connection connection = DataSource.getInstance().getConnection();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc where ton_tai = 1");
            while(rs.next()){
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
//        finally {
//            try{
//                connection.close();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }
       DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
     public static DateTime getDateTime(String dateTime){
         DateTime dayTime = new DateTime();
         String[] date_time = dateTime.split(" ");
         String[] date = date_time[0].split("-");
         String[] time = date_time[1].split(":");

         dayTime.setYear(Integer.parseInt(date[0]));
         dayTime.setMonth(Integer.parseInt(date[1]));
         dayTime.setDay(Integer.parseInt(date[2]));
         dayTime.setHour(Integer.parseInt(time[0]));
         dayTime.setMinute(Integer.parseInt(time[1]));
         dayTime.setSecond((int)Double.parseDouble(time[2]));
         return dayTime;
     }
    // l???y m???t danh m???c d???a v??o m?? danh m???c
    public static Category getCategoryById(String id){
        Connection connection = DataSource.getInstance().getConnection();
        try {
                PreparedStatement s = connection.prepareStatement("SELECT * FROM danh_muc WHERE ma_dm = ? and ton_tai = 1");
                s.setString(1, id);
                s.execute();
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getString("ma_dm"));
                    category.setName(rs.getString("ten_dm"));
                    String dateTime = rs.getString("ngay_tao");

//                  x??? l?? chu???i yyyy-mm-dd hh:mm:ss
                    DateTime dayTime = getDateTime(dateTime);
                    category.setDateCreated(dayTime);
                    category.setExist(rs.getInt("ton_tai"));

                    DataSource.getInstance().releaseConnection(connection);
                    rs.close();
                    s.close();

                    return category;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);
        System.out.println("Kh??ng t???n t???i danh m???c trong h??? th???ng");
        return null;
    }
    // hi???n th??? danh s??ch danh m???c theo m?? danh m???c t??ng d???n
    public static List<Category> getCategoriesByIdASC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ma_dm");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);
            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo m?? danh m???c gi???m d???n
    public static List<Category> getCategoriesByIdDESC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ma_dm DESC");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo t??n danh m???c t??ng d???n
    public static List<Category> getCategoriesByNameASC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ten_dm");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }

        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo t??n danh m???c gi???m d???n
    public static List<Category> getCategoriesByNameDESC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ten_dm DESC");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo ng??y t???o t??ng d???n
    public static List<Category> getCategoriesByDateCreatedASC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ngay_tao");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo ng??y t???o gi???m d???n
    public static List<Category> getCategoriesByDateCreatedDESC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1 ORDER BY ngay_tao DESC");
            while(rs.next()) {
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo s??? l?????ng s???n ph???m t??ng d???n
    public static List<Category> getCategoriesByProDuctQuantityASC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT danh_muc.maDM, count(*) as tong FROM danh_muc,sanpham " +
                    "WHERE danh_muc.maDM = sanpham.maDM GROUP BY danh_muc.maDM ORDER BY count(*)");
            // d??ng map l??u k???t qu???
            HashMap<Category,Integer> map = new HashMap<>();
            while(rs.next()) {
                map.put(getCategoryById(rs.getString(1)),rs.getInt(2));
            }
            for(Map.Entry<Category,Integer> ca : map.entrySet()){
                categories.add(ca.getKey());
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? danh s??ch danh m???c theo s??? l?????ng s???n ph???m gi???m d???n
    public static List<Category> getCategoriesByProDuctQuantityDESC(){
        Connection connection = DataSource.getInstance().getConnection();
        List<Category> categories = new ArrayList<>();
        try{
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT danh_muc.maDM, count(*) as tong FROM danh_muc,sanpham " +
                    "WHERE danh_muc.maDM = sanpham.maDM GROUP BY danh_muc.maDM ORDER BY count(*) DESC");
            // d??ng map l??u k???t qu???
            HashMap<Category,Integer> map = new HashMap<>();
            while(rs.next()) {
                map.put(getCategoryById(rs.getString(1)),rs.getInt(2));
            }
            for(Map.Entry<Category,Integer> ca : map.entrySet()){
                categories.add(ca.getKey());
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    public static List<Category> sort(String input){
        if(input.equals("1".toString())){
            return getCategoriesByDateCreatedASC();
        }
        else if(input.equals("2".toString())){
            return getCategoriesByNameASC();
        }
        else if(input.equals("3".toString())){
            return getCategoriesByIdASC();
        }
        return getAllCategories();
    }
    public ArrayList<Category> search(String input){
        Connection connection = DataSource.getInstance().getConnection();
        try {
            ArrayList<Category> categories = new ArrayList<>();
//            PreparedStatement s = connection.prepareStatement("SELECT * FROM danh_muc WHERE ? = ? OR ten_dm LIKE ? OR ngay_tao = ? AND ton_tai = 1");
            PreparedStatement s = connection.prepareStatement("SELECT * FROM danh_muc WHERE ten_dm LIKE ? AND ton_tai = 1");
            s.setString(1,"%"+input+"%");
//            s.setString(2,"ma_dm");
//            s.setString(1,"%"+input+"%");
            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                String dateTime = rs.getString("ngay_tao");
                DateTime dayTime = getDateTime(dateTime);
                category.setDateCreated(dayTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);

            }
            rs.close();
            s.close();
            DataSource.getInstance().releaseConnection(connection);
            return categories;
        }
        catch(Exception e){
            e.printStackTrace();
        }
//        finally {
//            try{
//                connection.close();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }
    // hi???n th??? s??? danh m???c t??? k???t qu??? t??m ki???m ????? ph??n trang (theo t??n danh m???c)
//    public static int numberOfPage(String input){
//        Connection connection = DataSource.getInstance().getConnection();
//        try {
//            PreparedStatement s = connection.prepareStatement("SELECT COUNT(*) FROM danh_muc WHERE ten_dm LIKE ?");
//            s.setString(1,"%"+input+"%");
//            ResultSet rs = s.executeQuery();
//            while(rs.next()){
//                return rs.getInt(1);
//            }
//            rs.close();
//            s.close();
//            DataSource.getInstance().releaseConnection(connection);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        DataSource.getInstance().releaseConnection(connection);
//        return 0;
//    }
    public static int numberOfPage(){
        Connection connection = DataSource.getInstance().getConnection();
        try {
            Statement s = connection.createStatement();

            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM danh_muc WHERE ton_tai = 1");
            while(rs.next()){
                return rs.getInt(1);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);


        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return 0;
    }
    // vid d??? 15 trang
    public static int getIndex(int index,int number){
        int result = number*(index-1)+1;
        return result;
    }
    public ArrayList<Category> getCategoriesByIndex(int index,int number){
        Connection connection = DataSource.getInstance().getConnection();
        try{
            ArrayList<Category> categories = new ArrayList<>();
            PreparedStatement s = connection.prepareStatement("select * from danh_muc WHERE ton_tai = 1 LIMIT ?,?");
            int value = getIndex(index,number);
            s.setInt(1,value-1);
            s.setInt(2,number);

            ResultSet rs = s.executeQuery();
            while(rs.next()){
                Category category = new Category();
                category.setId(rs.getString("ma_dm"));
                category.setName(rs.getString("ten_dm"));
                DateTime dateTime = getDateTime(rs.getString("ngay_tao"));
                category.setDateCreated(dateTime);
                category.setExist(rs.getInt("ton_tai"));
                categories.add(category);
            }
            rs.close();
            s.close();

            DataSource.getInstance().releaseConnection(connection);

            return categories;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        DataSource.getInstance().releaseConnection(connection);
        return new ArrayList<>();
    }

    //  Phu??ng th???c nh???n v?? list product admin, ??i???n t??n danh m???c cho n??
    public void fillNameForProductAdmin(List<ProductAdmin> products){

        Connection connection = DataSource.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ten_dm FROM danh_muc WHERE ma_dm = ?");
            for(ProductAdmin productAdmin:products){
                preparedStatement.setString(1,productAdmin.getDanh_muc().getId());
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                productAdmin.getDanh_muc().setName(resultSet.getString("ten_dm"));
                resultSet.close();
            }
            preparedStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DataSource.getInstance().releaseConnection(connection);

    }

    //  Phuownmg thuwc laya all dm
    public List<ProductAdminCategory> getAllCategory(){

        List<ProductAdminCategory> result = new ArrayList<ProductAdminCategory>();

        Connection connection = DataSource.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM danh_muc WHERE ton_tai = 1");
            while(resultSet.next()){
                ProductAdminCategory productAdminCategory = new ProductAdminCategory();
                productAdminCategory.setName(resultSet.getString("ten_dm"));
                productAdminCategory.setId(resultSet.getString("ma_dm"));
                result.add(productAdminCategory);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        DataSource.getInstance().releaseConnection(connection);

        return result;

    }

    //  Ph????ng th???uc nh???n v??o m?? dm tr??? v??? danh m???c
    public ProductAdminCategory getProductAdminCategoryById(String id){

        ProductAdminCategory productAdminCategory = new ProductAdminCategory();

        Connection connection =DataSource.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM danh_muc WHERE ma_dm = ?");
            preparedStatement.setString(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            productAdminCategory.setId(resultSet.getString("ma_dm"));
            productAdminCategory.setName(resultSet.getString("ten_dm"));
            resultSet.close();
            preparedStatement.close();
            DataSource.getInstance().releaseConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataSource.getInstance().releaseConnection(connection);

        return productAdminCategory;
    }


    public void fillDataProductAdminEditGroup(ProductAdminAdd productAdminEditGroup){

        Connection connection = DataSource.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT ten_dm FROM danh_muc WHERE ma_dm = ?");
            preparedStatement.setString(1,productAdminEditGroup.getDanh_muc().getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            productAdminEditGroup.getDanh_muc().setName(resultSet.getString("ten_dm"));
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DataSource.getInstance().releaseConnection(connection);

    }

    public static void main(String[] args) {
        CategoryWorksWithDatabase test = new CategoryWorksWithDatabase();

      // System.out.println(addCategory("??P"));
//        for(Category ca : categoryDAO.getCategoriesByIndex(1,3)){
//            System.out.println(ca);
//        }
       // System.out.println(test.updateCategory("dm_10","ao nu"));
        System.out.print(test.getProductNumberById("dm_1"));





        }




}
