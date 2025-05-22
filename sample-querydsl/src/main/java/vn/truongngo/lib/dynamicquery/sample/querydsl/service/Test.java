package vn.truongngo.lib.dynamicquery.sample.querydsl.service;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class Test {

    public static void main(String[] args) throws JSQLParserException {

        String column = "c.campaign_id as total";
        String predicate = "c.campaign_id = 1 and c.status = 1 and c.created_at > '2023-01-01'";
        String caseWhen = "CASE WHEN c.campaign_id IS NULL THEN 0 ELSE c.campaign_id END as id";
        String subquery = "(select name from employee where id = 1) as name";
        String literal = "1 as id";
        String windowFunction = "ROW_NUMBER() OVER (PARTITION BY c.campaign_id ORDER BY c.campaign_id) as row_number";

        PlainSelect plainSelect = (PlainSelect) CCJSqlParserUtil.parse("SELECT " + subquery);
        SelectItem<?> item = plainSelect.getSelectItems().get(0);
        String as = item.getAlias().getName();

        Expression expression = CCJSqlParserUtil.parseCondExpression(predicate);
        System.out.println(expression);

        System.out.println(as);
    }
}
