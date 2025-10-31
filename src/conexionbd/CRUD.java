package conexionbd;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author ariPi
 */
public class CRUD {
    
    private static String url = "jdbc:mysql://localhost:3306/empresa";
    private static String user = "super";
    private static String pass = "p4sSw0rD";
    
    public static Connection conectar() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexion Exitosa");
        } catch(SQLException e){
            System.out.println("ERROR de Conexion");
            e.printStackTrace();
        }
        return con;
    }
    
    public static void cerrar(Connection con) {
        try {
            if(con != null && !con.isClosed()){
                con.close();
                System.out.println("Conexion cerrada");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void insertar() {
        String tabla = JOptionPane.showInputDialog(null, "Ingrese la Tabla a la que va a Insertar datos", "Insertar Datos", JOptionPane.QUESTION_MESSAGE).toLowerCase();
        String query = "INSERT INTO " + tabla;
        switch (tabla) {
            case "sucursal":
                query += "(nombreSuc) values (?)";
                break;
            case "departamento":
                query += "(nombreDep, id_sucursal) values (?, ?)";
                break;
            case "empleado":
                query += "(nombreEmp, id_departamento) values (?, ?)";
                break;
            default:
                JOptionPane.showMessageDialog(null, "No se identifico esa tabla", "ERROR DE TABLA", JOptionPane.ERROR_MESSAGE);
                return;
        }
        try {
            Connection con = conectar();
            PreparedStatement ps = con.prepareStatement(query);
            switch (tabla) {
            case "sucursal":
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nombre de la Nueva sucursal", "Nombre Sucursal", JOptionPane.QUESTION_MESSAGE));
                break;                                  
            case "departamento":
                int idSuc;
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nombre del Nuevo Departamento", "Nombre Departamento", JOptionPane.QUESTION_MESSAGE));
                try {
                    idSuc = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID de la Sucursal a la que el Nuevo Departamento esta recionado", "ID Sucursal - Departamento", JOptionPane.QUESTION_MESSAGE));
                    if (idSuc < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE VALOR", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(2, idSuc);
                break;
            case "empleado":
                int idDep;
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nombre del Nuevo Empleado", "Nombre Empleado", JOptionPane.QUESTION_MESSAGE));
                try {
                    idDep = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID del Departamento al que el Nuevo Empleado esta recionado", "ID Departamento - Empleado", JOptionPane.QUESTION_MESSAGE));
                    if (idDep < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(2, idDep);
                break;
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, tabla + " Cread@ con exito!");
            cerrar(con);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar Dato", "ERROR DE DATOS", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void leer() {
        String tabla = JOptionPane.showInputDialog(null, "Ingrese la Tabla de la que quiere Saber Datos", "Insertar Datos", JOptionPane.QUESTION_MESSAGE).toLowerCase();
        try {
            if(Integer.parseInt(tabla) == JOptionPane.CANCEL_OPTION) return;
        } catch(NumberFormatException e) {
        }
        String query = "SELECT ";
        String msg;
        switch (tabla) {
            case "sucursal":
                query += "* FROM sucursal";
                msg = """
                         <html>
                            <body>
                                <table border="1">
                                    <tr>
                                        <th>ID Sucursal</th>
                                        <th>Nombre Sucursal</th>
                                    </tr>
                      """;
                break;
            case "departamento":
                query += "d.*, s.nombreSuc FROM departamento d JOIN sucursal s ON d.id_sucursal = s.id_sucursal";
                msg = """
                      <html>
                        <body>
                            <table border="1">
                                <tr>
                                    <th>ID Departamento</th>
                                    <th>Nombre Departamento</th>
                                    <th>ID Sucursal</th>
                                    <th>Nombre Sucursal</th>
                                </tr>
                      """;
                break;
            case "empleado":
                query += "e.*, d.nombreDep FROM empleado e JOIN departamento d ON e.id_departamento = d.id_departamento";
                msg = """
                        <html>
                            <body>
                                <table border="1">
                                    <tr>
                                        <th>ID Nomina Empleado</th>
                                        <th>Nombre Empleado</th>
                                        <th>ID Departamento</th>
                                        <th>Nombre Departamento</th>
                                    </tr>
                      """;
                break;
            default:
                JOptionPane.showMessageDialog(null, "No se identifico esa tabla", "ERROR DE TABLA", JOptionPane.ERROR_MESSAGE);
                return;
        }
        try {
            Connection con = conectar();
            PreparedStatement ps = con.prepareStatement(query);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                switch (tabla) {
                    case "sucursal":
                        msg += String.format(
                               """
                                       <tr>
                                           <td>%d</td>
                                           <td>%s</td>
                                       </tr>
                               """, 
                                rs.getInt("id_sucursal"),
                                rs.getString("nombreSuc"));
                        break;                                  
                    case "departamento":
                        msg += String.format( 
                               """
                                       <tr>
                                           <td>%d</td>
                                           <td>%s</td>
                                           <td>%d</td>
                                           <td>%s</td>
                                       </tr>
                               """, 
                                rs.getInt("id_departamento"),
                                rs.getString("nombreDep"),
                                rs.getInt("id_sucursal"),
                                rs.getString("nombreSuc"));
                        break;
                    case "empleado":
                        msg += String.format(
                               """
                                       <tr>
                                           <td>%d</td>
                                           <td>%s</td>
                                           <td>%d</td>
                                           <td>%s</td>
                                       </tr>
                               """, 
                                rs.getInt("id_nomina"),
                                rs.getString("nombreEmp"),
                                rs.getInt("id_departamento"),
                                rs.getString("nombreDep"));
                        break;
                }
            }
            msg += """
                                </table>
                            </body>
                        </html>
                   """;
            cerrar(con);
            JOptionPane.showMessageDialog(null, msg, "Datos Obtenidos con Exito!", JOptionPane.INFORMATION_MESSAGE);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al Verificar Dato", "ERROR DE DATOS", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void modificar() {
        String tabla = JOptionPane.showInputDialog(null, "Ingrese la Tabla a la que va a Modificar Datos", "Modificar Datos", JOptionPane.QUESTION_MESSAGE).toLowerCase();
        String query = "UPDATE " + tabla + " SET ";
        switch (tabla) {
            case "sucursal":
                query += "nombreSuc = ? WHERE id_sucursal = ?";
                break;
            case "departamento":
                query += "nombreDep = ? WHERE id_departamento = ?";
                break;
            case "empleado":
                query += "nombreEmp = ? WHERE id_nomina = ?";
                break;
            default:
                JOptionPane.showMessageDialog(null, "No se identifico esa tabla", "ERROR DE TABLA", JOptionPane.ERROR_MESSAGE);
                return;
        }
        try {
            Connection con = conectar();
            PreparedStatement ps = con.prepareStatement(query);
            switch (tabla) {
            case "sucursal":
                int idSuc;
                try {
                    idSuc = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID de la Sucursal que desea Actualizar", "ID Sucursal", JOptionPane.QUESTION_MESSAGE));
                    if (idSuc < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE VALOR", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(2, idSuc);
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nuevo Nombre de la Sucursal", "Nombre Sucursal", JOptionPane.QUESTION_MESSAGE));
                break;                                  
            case "departamento":
                int idDep;
                try {
                    idDep = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID del Departamento que desea Actualizar", "ID Departamento", JOptionPane.QUESTION_MESSAGE));
                    if (idDep < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE VALOR", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(2, idDep);
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nuevo Nombre del Departamento", "Nombre Departamento", JOptionPane.QUESTION_MESSAGE));
                break;
            case "empleado":
                int idEmp;
                try {
                    idEmp = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID de Nomina del Empleado que desea Actualizar", "ID Empleado", JOptionPane.QUESTION_MESSAGE));
                    if (idEmp < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(2, idEmp);
                ps.setString(1, JOptionPane.showInputDialog(null, "Ingrese el Nuevo Nombre del Empleado", "Nombre Empleado", JOptionPane.QUESTION_MESSAGE));
                break;
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, tabla + " Acualizado@ con exito!");
            cerrar(con);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al Actualizar Dato", "ERROR DE DATOS", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public void eliminar() {
        String tabla = JOptionPane.showInputDialog(null, "Ingrese la Tabla a la que va a Eliminar Datos", "Eliminar Datos", JOptionPane.QUESTION_MESSAGE).toLowerCase();
        String query = "DELETE FROM " + tabla;
        switch (tabla) {
            case "sucursal":
                query += " WHERE id_sucursal = ?";
                break;
            case "departamento":
                query += " WHERE id_departamento = ?";
                break;
            case "empleado":
                query += " WHERE id_nomina = ?";
                break;
            default:
                JOptionPane.showMessageDialog(null, "No se identifico esa tabla", "ERROR DE TABLA", JOptionPane.ERROR_MESSAGE);
                return;
        }
        try {
            Connection con = conectar();
            PreparedStatement ps = con.prepareStatement(query);
            switch (tabla) {
            case "sucursal":
                int idSuc;
                try {
                    idSuc = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID de la Sucursal que desea Eliminar", "ID Sucursal", JOptionPane.QUESTION_MESSAGE));
                    if (idSuc < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE VALOR", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(1, idSuc);
                break;                                  
            case "departamento":
                int idDep;
                try {
                    idDep = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID del Departamento que desea Eliminar", "ID Departamento", JOptionPane.QUESTION_MESSAGE));
                    if (idDep < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE VALOR", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(1, idDep);
                break;
            case "empleado":
                int idEmp;
                try {
                    idEmp = Integer.parseInt(JOptionPane.showInputDialog(null, "Ingrese el ID de Nomina del Empleado que desea Eliminar", "ID Empleado", JOptionPane.QUESTION_MESSAGE));
                    if (idEmp < 1) {
                        JOptionPane.showMessageDialog(null, "Numero Invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                        cerrar(con);
                        return;
                    }
                } catch(NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor invalido", "ERROR DE FORMATO", JOptionPane.ERROR_MESSAGE);
                    cerrar(con);
                    return;
                }
                ps.setInt(1, idEmp);
                break;
            }
            if (JOptionPane.showConfirmDialog(null, "Esta Seguro de que desea Eliminar esta Informacion, Existe la eliminacion en Cascasda y cualquier Informacion relacionada a lo que desea Elimninar Tambien sera Eliminada", "Confirmacion de Eliminacion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION){
                cerrar(con);
                return;
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, tabla + " Eliminad@ con exito!");
            cerrar(con);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al Eliminar Dato", "ERROR DE DATOS", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
}
