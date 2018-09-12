package usgbiomatricsapp.customs.usg.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import javax.swing.JOptionPane;
import usgbiomatricsapp.customs.Global;
import usgbiomatricsapp.customs.usg.pojo.Employee;
import usgbiomatricsapp.customs.usg.pojo.Thumb;

/**
 *
 * @author usmanriaz
 */
public class DBHandler {

    private Employee[] employees;
    private int totalEmps;

    public DBHandler() {

        totalEmps = getEmployeeCount();

        employees = new Employee[totalEmps];
    }

    private int getEmployeeCount() {
        System.out.println("DBHandler(getEmployeeCount())Selecting Total of " + Global.operatingUnit);
        String query = null;
        if (Global.operatingUnit == null) {
            query = "select count(*) cnt from dual";
        } else {
            if (Global.operatingUnit.equals("UNIT 1")) {

                query = "select count(*) cnt from shift_harmony where TIMEOFFICE1 = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("UNIT 2")) {

                query = "select count(*) cnt from shift_harmony where TIMEOFFICE2 = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("UNIT 3/4")) {

                query = "select count(*) cnt from shift_harmony where (TIMEOFFICE3 = 'Y' or TIMEOFFICE4 = 'Y') and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("UNIT 5")) {

                query = "select count(*) cnt from shift_harmony where TIMEOFFICE5 = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("MANAGEMENT")) {
                query = "select count(*) cnt from shift_harmony where US5MT = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("FEMALE")) {
                query = "select count(*) cnt from shift_harmony where US5FEMALE = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("Corporate")) {
                query = "select count(*) cnt from shift_harmony where CORPORATE = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("Line 7")) {
                query = "select count(*) cnt from shift_harmony where line7 = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("Finance")) {
                query = "select count(*) cnt from shift_harmony where finance = 'Y' and active = 'Y' and xml_p is not null";
            } else if (Global.operatingUnit.equals("Fashion")) {
                query = "select count(*) cnt from shift_harmony where fasion = 'Y' and active = 'Y' and xml_p is not null";
            }
        }
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int count = 0;
        try {
            Connection con = DBConnection.getInstance().getConnection();
            if (con != null) {
                pst = con.prepareStatement(query);
                rs = pst.executeQuery();

                if (rs.next()) {
                    count = rs.getInt("cnt");
                }
            } else {
                return 0;
            }

        } catch (Exception ex) {
            System.out.println("DbHandler(getEmployeeCount):" + ex.getMessage());
            count = 0;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

        return count;
    }

    public Employee[] getEmployees(String operatingUnit) {
        // if count returns 0 then simply return 0 employee array and this method's execution
        if (totalEmps == 0) {
            return new Employee[totalEmps];
        }

        Employee[] empArray = new Employee[totalEmps];
        String query = null;
        if (operatingUnit.equals("UNIT 1")) {
            query = "select * from shift_harmony where TIMEOFFICE1 = 'Y' and active = 'Y' and xml_p is not null";
        } else if (operatingUnit.equals("UNIT 2")) {
            query = "select * from shift_harmony where TIMEOFFICE2 = 'Y' and active = 'Y' and xml_p is not null";
        } else if (operatingUnit.equals("UNIT 3/4")) {
            query = "select * from shift_harmony where (TIMEOFFICE3 = 'Y' or TIMEOFFICE4 = 'Y') and active = 'Y' and xml_p is not null";
        } else if (operatingUnit.equals("UNIT 5")) {
            query = "select * from shift_harmony where TIMEOFFICE5 = 'Y' and active = 'Y' and xml_p is not null";
        } else if (operatingUnit.equals("MANAGEMENT")) {
            query = "select * from shift_harmony where us5mt = 'Y' and active = 'Y' and xml_p is not null";
        } else if (Global.operatingUnit.equals("FEMALE")) {
            query = "select * from shift_harmony where US5FEMALE = 'Y' and active = 'Y' and xml_p is not null";
        } else if (Global.operatingUnit.equals("Corporate")) {
            query = "select * from shift_harmony where CORPORATE = 'Y' and active = 'Y' and xml_p is not null";
        } else if (Global.operatingUnit.equals("Line 7")) {
            query = "select * from shift_harmony where line7 = 'Y' and active = 'Y' and xml_p is not null";
        } else if (Global.operatingUnit.equals("Finance")) {
            query = "select * from shift_harmony where finance = 'Y' and active = 'Y' and xml_p is not null";
        } else if (Global.operatingUnit.equals("Fashion")) {
            query = "select * from shift_harmony where fasion = 'Y' and active = 'Y' and xml_p is not null";
        }
        /*else if (operatingUnit.equals("STYLERS")){
            query = "select * from shift_harmony where TIMEOFFICE1 = 'Y' and active = 'Y' and xml_p is not null";
        }else if (operatingUnit.equals("AFL")){
            query = "select * from shift_harmony where TIMEOFFICE1 = 'Y' and active = 'Y' and xml_p is not null";
        }*/

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Connection con = DBConnection.getInstance().getConnection();
            if (con != null) {
                pst = con.prepareStatement(query);
                rs = pst.executeQuery();
                int index = 0;
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setActive(rs.getString("ACTIVE"));
                    emp.setDepartment(rs.getString("DPT"));
                    emp.setDesignation(rs.getString("DESIG"));
                    emp.setEmpCode(rs.getString("EMP_CODE"));
                    emp.setFatherName(rs.getString("FATHER_NAME"));
                    emp.setName(rs.getString("NAME"));
                    emp.setShiftID(rs.getString("SHIFT_ID"));
                    emp.setShiftName(rs.getString("SHIFT_NAME"));
                    //emp.setUnitName(rs.getString(""));
                    emp.setXml_p(rs.getString("XML_P"));
                    emp.setDeptID(rs.getString("DEPT_ID"));
                    empArray[index] = emp;
                    index += 1;
                }
                return empArray;
            } else {

                return new Employee[0];
            }

        } catch (Exception ex) {
            System.out.println("DBHandler(getEmployees):" + ex.getMessage());
            return new Employee[0];
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

    }

    public int updateEmployee(String thumbXml, String employeeCode, String fingerCode) {
        String updateEmployee = "";

        updateEmployee = "update shift_harmony set " + (thumbXml.equals("NO_UPDATE") ? "xml_p = xml_p " : "xml_p='" + thumbXml + "'") + "," + (fingerCode.equals("NF") ? "finger_code = finger_code" : "finger_code='" + fingerCode + "'") + " where emp_code =  '" + employeeCode + "'";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        int update = -1;
        try {
            Connection con = DBConnection.getInstance().getConnection();
            pst = con.prepareStatement(updateEmployee);
            update = pst.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

        return update;

    }

    public String isLeavingEarly(String empcode) {

        String query = "SELECT ustms.CUST_IS_LEAVING_EARLY(?) EARLY FROM DUAL";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String early = null;
        int update = -1;
        try {
            Connection con = DBConnection.getInstance().getConnection();
            pst = con.prepareStatement(query);
            pst.setString(1, empcode);
            rs = pst.executeQuery();
            if (rs.next()) {
                early = rs.getString("EARLY");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

        return early;

    }

    public String isComingLate(String empcode) {

        String query = "SELECT ustms.cust_is_coming_late(?) late FROM DUAL";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String late = null;
        int update = -1;
        try {
            Connection con = DBConnection.getInstance().getConnection();
            pst = con.prepareStatement(query);
            pst.setString(1, empcode);
            rs = pst.executeQuery();
            if (rs.next()) {
                late = rs.getString("late");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

        return late;

    }

    public String apply(String empcode, java.sql.Timestamp fromDate, java.sql.Timestamp toDate, String leaveType, String deptID) {

        String query = "{ call ustms.CUST_APPLY_LEAVE_BIOMATRIC(?,?,?,?,?,?)}";

        Connection conn = null;
        CallableStatement cst = null;

        try {
            Connection con = DBConnection.getInstance().getConnection();
            cst = con.prepareCall(query);
            cst.setString(1, empcode);
            cst.setTimestamp(2, fromDate);
            cst.setTimestamp(3, toDate);
            cst.setString(4, leaveType);
            cst.setString(5, deptID);
            cst.registerOutParameter(6, Types.VARCHAR);
            cst.execute();
            String result = cst.getString(6);
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "INTERNAL_ERROR";
        } finally {
            try {

                if (cst != null) {
                    cst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex3) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex3.getMessage());
            }
        }

    }

    public int slDailyLimitReachedQuestionMark(String employeeCode) {
        CallableStatement cst = null;
        Connection con = null;
        int returnCode = -1;

        try {
            con = DBConnection.getInstance().getConnection();
            cst = con.prepareCall("{? = call USTMS.daily_limit(?)}");
            cst.registerOutParameter(1, Types.INTEGER);
            cst.setString(2, employeeCode);
            cst.executeUpdate();
            returnCode = cst.getInt(1);
        } catch (Exception ex) {
            System.out.println("Error Logging Attendance: " + ex.getMessage());

        } finally {
            try {

                if (cst != null) {
                    cst.close();
                }
                DBConnection.closeConnection();

            } catch (Exception ex) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex.getMessage());
            }
        }
        return returnCode;
    }

    public int logAttendance(String employeeCode) {
        CallableStatement cst = null;
        Connection con = null;
        int returnCode = -1;

        try {
            con = DBConnection.getInstance().getConnection();
            cst = con.prepareCall("{? = call USTMS.INOUT_PROC(?)}");
            cst.registerOutParameter(1, Types.INTEGER);
            cst.setString(2, employeeCode);
            cst.executeUpdate();
            returnCode = cst.getInt(1);
        } catch (Exception ex) {
            System.out.println("Error Logging Attendance: " + ex.getMessage());

        } finally {
            try {

                if (cst != null) {
                    cst.close();
                }
                DBConnection.closeConnection();

            } catch (Exception ex) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex.getMessage());
            }
        }
        return returnCode;
    }

    public Employee getEmployee(String empCode) {
        String selectEmployee = "select sh.emp_code, eiv.name,sh.active, sh.xml_p,"
                + "eiv.father,"
                + "eiv.designation,"
                + "eiv.department,"
                + "eiv.co_id,"
                + "sh.shift_name,"
                + "sh.shift_id ,"
                + "nvl(sh.finger_code,'NF') finger_code , "
                + "eiv.DEPT_ID "
                + "from shift_harmony sh,"
                + "employees_information_V eiv "
                + "where sh.emp_code = eiv.emp_code "
                + "and sh.emp_code = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection conn = null;
        Employee emp = new Employee();
        try {
            conn = DBConnection.getInstance().getConnection();
            pst = conn.prepareStatement(selectEmployee);
            pst.setString(1, empCode);
            rs = pst.executeQuery();
            if (rs.next()) {
                //emp.setEmpCode(Integer.parseInt(rs.getString("emp_code")));
                emp.setDesignation(rs.getString("DESIGNATION"));
                emp.setName(rs.getString("NAME"));
                emp.setFatherName(rs.getString("FATHER"));
                emp.setDepartment(rs.getString("DEPARTMENT"));
                emp.setUnitName(String.valueOf(rs.getInt("CO_ID")));
                emp.setShiftName(rs.getString("SHIFT_NAME"));
                emp.setShiftID(rs.getString("SHIFT_ID"));
                emp.setXml_p(rs.getString("XML_P"));
                emp.setActive(rs.getString("ACTIVE"));
                emp.setEmpCode(rs.getString("emp_code"));
                emp.setFingerCode(rs.getString("finger_code"));
                emp.setDeptID(rs.getString("DEPT_ID"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            //JOptionPane.showMessageDialog(null, "FPDialogRegister->Search:" + ex.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                DBConnection.closeConnection();
            } catch (Exception ex) {
                System.out.println("Unable to close Connection/ResultSet/PreparedStatement:" + ex.getMessage());
            }
        }
        return emp;
    }

}
