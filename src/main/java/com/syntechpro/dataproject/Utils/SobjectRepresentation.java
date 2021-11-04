package com.syntechpro.dataproject.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SobjectRepresentation {
    public String name;
    public Map<String,Field> fields;
    public SobjectRepresentation(String name)
    {
        this.name = name;
        fields = new HashMap();
    }
    public void addField(String label,String name,Class T)
    {
        fields.put(label,new Field(label,name,T));
    }
    public void addField(String label,String name,Class T,Object value)
    {
        fields.put(label,new Field(label,name,T,value));
    }
    @Override
    public String toString()
    {
        String base = "";
        for(Field f:fields.values())
        {
            base+=f.toStringSystem()+"\n";
        }
        return base;
    }
    public List<String> getAllValue()
    {
        List<String> str = new ArrayList<>();
        for(Field f:fields.values())
        {
            str.add(f.label);
        }
        return str;
    }
    public String getFieldsAPI(String label)
    {
        return fields.get(label).name;
    }
    public String getFields(String label)
    {
        return fields.get(label).toStringSystem();
    }
}

class Field
{
    public String label;
    public String name;
    public Class classOfField;
    public Object value;

    public Field(String label,String Name,Class className)
    {
        this.label = label;
        this.name = Name;
        classOfField = className;
    }

    public Field(String label,String Name,Class className,Object value)
    {
        this.label = label;
        this.name = Name;
        classOfField = className;
        this.value = value;
    }

    public String getClassExtends()
    {
        return classOfField.toString();
    }

    @Override
    public String toString() {
        return name;
    }
    public String toStringSystem() {
        if(value !=  null)
            return name + " : " + label +"\n" + value.toString();
        else
        {
            return name + " : " + label +"\n" + classOfField.toString();
        }
    }
}
