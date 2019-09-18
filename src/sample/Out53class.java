package sample;




import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Out53class extends Response {
    public Out53class(String value, Connection connection, OutputStream outputStream, Statement statement) throws SQLException {
        super(value, connection, outputStream,statement);
    }
    public String preConnection(){
        String bc_data = value.substring(28, 42);
        String second_sql = String.format("select * from test.ID_03 where bc_data = '%s';", bc_data);
        return second_sql;
    }
    public void postConnection(ResultSet second_result) throws SQLException, InterruptedException  {
        String item_code = "";
        String item_name1 = "";
        String item_name2 = "";
        String color_code = "";
        String plan_qty = "";
        String weight_type = "";
        String number_of_boxes = "";
        String unit_weight_type = "";
        String range_min_qty = "";
        String range_max_qty = "";
        String unit_weight = "";
        String range_min_unit_weight = "";
        String range_max_unit_weight = "";
        String response_code = "";
        String control_info = "";
        while (second_result.next()) {
            result_cnt += 1;
            item_code = second_result.getString("item_code");
            item_name1 = second_result.getString("item_name1");
            item_name2 = second_result.getString("item_name2");
            color_code = second_result.getString("color_code");
            plan_qty = second_result.getString("plan_qty");
            weight_type = second_result.getString("weight_type");
            number_of_boxes = second_result.getString("number_of_boxes");
            unit_weight_type = second_result.getString("unit_weight_type");
            range_min_qty = second_result.getString("range_min_qty");
            range_max_qty = second_result.getString("range_max_qty");
            unit_weight = second_result.getString("unit_weight");
            range_min_unit_weight = second_result.getString("range_min_unit_weight");
            range_max_unit_weight = second_result.getString("range_max_unit_weight");
            response_code = second_result.getString("response_code");
            control_info = second_result.getString("control_info");
        }
        if (result_cnt == 0){
            item_code = "       ";
            item_name1 = "                                   ";
            item_name2 = "                                   ";
            color_code = "     ";
            plan_qty = "                 ";
            weight_type = "   ";
            number_of_boxes = "   ";
            unit_weight_type = "   ";
            range_min_qty = "                 ";
            range_max_qty = "                 ";
            unit_weight = "          ";
            range_min_unit_weight = "          ";
            range_max_unit_weight = "          ";
            response_code = "99999999";
            control_info = "                              ";
        }
        final_result = String.format("%04d", Controller.seq_num_send) + "03" + value.substring(6, 8) + date() + value.substring(14, 42) + value.substring(48, 62)+ item_code + item_name1 + item_name2 + color_code + plan_qty + weight_type + number_of_boxes + unit_weight_type + range_min_qty + range_max_qty + unit_weight + range_max_unit_weight + range_max_unit_weight + response_code + control_info;
    }
}