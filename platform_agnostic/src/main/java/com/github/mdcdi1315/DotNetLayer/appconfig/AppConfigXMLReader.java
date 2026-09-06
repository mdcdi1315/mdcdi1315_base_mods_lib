package com.github.mdcdi1315.DotNetLayer.appconfig;

import com.github.mdcdi1315.DotNetLayer.System.Action2;
import com.github.mdcdi1315.DotNetLayer.System.AppContext;
import com.github.mdcdi1315.DotNetLayer.System.ArgumentNullException;
import com.github.mdcdi1315.DotNetLayer.System.InvalidOperationException;
import com.github.mdcdi1315.DotNetLayer.System.Collections.DictionaryEntry;
import com.github.mdcdi1315.DotNetLayer.System.Diagnostics.CodeAnalysis.NotNull;
import com.github.mdcdi1315.DotNetLayer.System.Collections.Specialized.StringDictionary;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.io.InputStream;

public final class AppConfigXMLReader
{
    private AppConfigXMLReader() {}

    @NotNull
    public static StringDictionary ReadFromStream(InputStream stream)
            throws ArgumentNullException, IOException
    {
        ArgumentNullException.ThrowIfNull(stream, "inputStream");
        StringDictionary result = new StringDictionary();
        try {
            XMLStreamReader reader = XMLInputFactory.newDefaultFactory().createXMLStreamReader(stream);
            while (reader.hasNext())
            {
                if (reader.next() == XMLStreamReader.START_ELEMENT && "appSettings".equals(reader.getLocalName()))
                {
                    DecodeXMLData(reader, result);
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        return result;
    }

    public static void ApplyByConsumerFunction(StringDictionary settings, Action2<String, String> consumer)
            throws ArgumentNullException, InvalidOperationException
    {
        ArgumentNullException.ThrowIfNull(settings, "settings");
        ArgumentNullException.ThrowIfNull(consumer, "consumer");
        var en = settings.GetEnumerator();
        while (en.MoveNext())
        {
            DictionaryEntry e = (DictionaryEntry)en.getCurrent();
            String key = (String) e.GetKey(), value = (String) e.GetValue();
            if (key.startsWith("AppContext")) {
                if (key.startsWith("AppContext.SetSwitch:")) {
                    AppContext.SetSwitch(key.substring(key.indexOf(':')+1), Boolean.parseBoolean(value));
                } else if (key.startsWith("AppContext.SetData:")) {
                    AppContext.SetData(key.substring(key.indexOf(':')+1), value);
                } else {
                    throw new InvalidOperationException("Invalid AppContext key definition: " + key);
                }
            } else {
                consumer.action(key, value);
            }
        }
    }

    public static void ApplyToApplicationContext(StringDictionary settings)
            throws ArgumentNullException
    {
        ApplyByConsumerFunction(settings, AppContext::SetData);
    }

    private static void DecodeXMLData(XMLStreamReader reader, StringDictionary dictionary)
            throws XMLStreamException
    {
        while (reader.hasNext())
        {
            if (reader.next() == XMLStreamReader.START_ELEMENT)
            {
                String attr_name;
                switch (reader.getLocalName())
                {
                    case "add":
                        attr_name = reader.getAttributeValue(null, "key");
                        if (attr_name == null) {
                            throw new XMLStreamException("Missing required attribute 'key'");
                        }
                        String attr_value = reader.getAttributeValue(null, "value");
                        if (attr_value == null) {
                            throw new XMLStreamException("Missing required attribute 'value'");
                        }
                        dictionary.Add(attr_name, attr_value);
                        break;
                    case "remove":
                        attr_name = reader.getAttributeValue(null, "key");
                        if (attr_name == null) {
                            throw new XMLStreamException("Missing required attribute 'key'");
                        }
                        dictionary.Remove(attr_name);
                        break;
                    case "clear":
                        dictionary.Clear();
                        break;
                }
            }
        }
    }
}
