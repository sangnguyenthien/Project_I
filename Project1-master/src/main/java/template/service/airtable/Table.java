package template.service.airtable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Table{
    private int i;
    private final String id;
    private final String name;
    private final List<Field> fields = new ArrayList<>();

    private final List<Record> records = new ArrayList<>();

    protected String getName() {
        return this.name;
    }
    protected String getId() {
        return this.id;
    }
    protected int getI() {
        return this.i;
    }
    protected int getNumRecords() {
        return records.size();
    }

    protected Field getField(String name)
    {
        for (Field field : this.fields)
        {
            if (field.getName().equals(name))
            {
                return field;
            }
        }
        return null;
    }
    protected Table(JsonObject table, String baseId, String personal_access_token)
    {
        this.id = table.get("id").getAsString();
        this.name = table.get("name").getAsString();
        table.get("fields").getAsJsonArray().forEach(field -> this.fields.add(new Field(field.getAsJsonObject())));

        syncRecord(baseId, personal_access_token);
    }

    protected void syncRecord(String baseId, String personal_access_token)
    {
        records.clear();
        String records = Record.listRecords(id, baseId, personal_access_token);
        if (records == null){
            System.out.println("Error: Cannot get records in table: " + name);
        }
        else
        {
            JsonObject recordsJson = new Gson().fromJson(records, JsonObject.class);
            JsonArray listRecords = recordsJson.get("records").getAsJsonArray();
            listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));
        }
    }

    protected boolean addField(JsonObject field, String baseId, String token) {
        String fieldCreate = Field.createField(field, id, baseId, token);
        if (fieldCreate == null) {
            System.out.println("Error: Cannot create field: " + field.get("name").getAsString() + " in table: " + name);
            return false;
        }
        JsonObject fieldJson = JsonParser.parseString(fieldCreate).getAsJsonObject();
        fields.add(new Field(fieldJson));
        System.out.println("Created field: " + field.get("name").getAsString() + " in table: " + name);
        return true;
    }

    private boolean updateRecord(JsonObject fields, Record record, String baseId, String personal_access_token)
    {
        String recordUpdate = Record.updateRecord(fields, record.getId(), id, baseId, personal_access_token);
        if (recordUpdate == null)
        {
            return false;
        }
        JsonObject recordJson = JsonParser.parseString(recordUpdate).getAsJsonObject();
        records.remove(record);
        records.add(new Record(recordJson));
        //print
        return true;
    }

    private boolean addRecord(JsonObject fields, String baseId, String personal_access_token)
    {
        String recordCreate = Record.createRecord(fields, id, baseId, personal_access_token);
        if (recordCreate == null)
        {
            //print
            return false;
        }
        JsonObject recordJson = new Gson().fromJson(recordCreate, JsonObject.class);
        JsonArray listRecords = recordJson.get("records").getAsJsonArray();

        //records.clear();
        listRecords.forEach(record -> this.records.add(new Record(record.getAsJsonObject())));

        //print
        return true;
    }

    protected Record getRecord(String idField)
    {
        for (Record record : this.records)
        {
            if (record.getIdField().equals(idField))
            {
                return record;
            }
        }
        return null;
    }

    private boolean pullRecord(JsonObject fields, String baseId, String token)
    {
        Record oldRec = getRecord(fields.get("id").getAsString());
        if (oldRec == null)
        {
            if (addRecord(fields, baseId, token))
            {
                //print("Add record to table name"
                return true;
            }
            return false;
        }
        if (oldRec.equals(fields, this.fields))
        {
            return true;
        }

        if (updateRecord(fields, oldRec, baseId, token))
        {
            //print Update record: record ID in table
            return true;
        }
        return false;
    }

    private boolean pullAllRecords(List<JsonObject> fields, String baseId, String personal_access_token)
    {
        for (JsonObject field : fields)
        {
            if (!pullRecord(field, baseId, personal_access_token))
            {
                //print Cannot pull record: user ID in table name
                return false;
            }
        }
        //print Pulled all records in table name
        return true;
    }

    private void dropRecord(List<JsonObject> fields, String baseId, String personal_access_token)
    {
        List<Record> dropList = new ArrayList<>();
        for (Record record : this.records)
        {
            boolean isExist = false;
            for (JsonObject field : fields)
            {
                if (record.getIdField().equals(field.get("id").getAsString()))
                {
                    isExist = true;
                    break;
                }
            }
            if (!isExist)
            {
                if (Record.dropRecord(record.getId(), id, baseId, personal_access_token))
                {
                    //print Deleted record: record.getIdField() in table name
                    dropList.add(record);
                }
                else
                {
                    //print cannot delete record: record.getIdField
                }
            }
        }
        this.records.removeAll(dropList);
    }



}
