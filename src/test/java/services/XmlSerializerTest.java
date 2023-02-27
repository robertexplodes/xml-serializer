package services;

import org.junit.jupiter.api.*;
import services.classes.Collections;
import services.classes.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class XmlSerializerTest {

    @Nested
    class Serializing {

        @Test
        void object_without_fields() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new NoFields());

            assertThat(serialized)
                    .isEqualToIgnoringWhitespace("""
                            <NoFields>
                            </NoFields>
                            """);
        }

        @Test
        void object_with_primitive_fields() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new PrimitiveFields('#', 42, true));

            assertThat(serialized)
                    .isEqualToIgnoringWhitespace("""
                            <PrimitiveFields>
                                <someChar>
                                    #
                                </someChar>
                                <someLong>
                                    42
                                </someLong>
                                <someBoolean>
                                    true
                                </someBoolean>
                            </PrimitiveFields>
                            """);
        }

        @Test
        void ignores_fake_getters() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new FakeGetters(317));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <FakeGetters>
                        <value>
                            317
                        </value>
                    </FakeGetters>
                    """);
        }

        @Test
        void ignores_null_fields() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new Strings("null", null));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Strings>
                        <property>
                            null
                        </property>
                    </Strings>
                    """);
        }

        @Test
        void object_with_wrappers() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new Wrappers(42, null));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Wrappers>
                        <wrappedInt>
                            42
                        </wrappedInt>
                    </Wrappers>
                    """);
        }

        @Test
        void object_with_strings() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new Strings("value", "tim"));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Strings>
                        <property>
                            value
                        </property>
                        <name>
                            tim
                        </name>
                    </Strings>
                    """);
        }

        @Test
        void special_xml_characters() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new Strings(null, "< > & ' \""));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Strings>
                        <name>
                            &lt &gt &amp &apos &quot
                        </name>
                    </Strings>
                    """
            );
        }

        @Test
        void object_with_object_arrays() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new ObjectArrays(
                    new Integer[]{317, 42},
                    new String[]{"single"},
                    new Boolean[0]));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <ObjectArrays>
                        <ints>
                            <value>317</value>
                            <value>42</value>
                        </ints>
                        <strings>
                            <value>single</value>
                        </strings>
                        <booleans>
                        </booleans>
                    </ObjectArrays>
                    """);
        }

        @Test
        void composite_objects() {
            var serializer = new XmlSerializer();
            var composite = new Composite(new Part(1), new Part(2));

            var serialized = serializer.serialize(composite);

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Composite>
                        <part1>
                            <Part>
                                <size>1</size>
                            </Part>
                        </part1>
                        <part2>
                            <Part>
                                <size>2</size>
                            </Part>
                        </part2>
                    </Composite>
                    """);
        }

        @Test
        void object_with_collections() {
            var serializer = new XmlSerializer();
            var map = new LinkedHashMap<Integer, Strings>();
            map.put(1, new Strings("one", "_"));
            map.put(2, new Strings("two", "_"));
            var collections = new Collections(
                    List.of("lorem", "ipsum"),
                    Set.of(),
                    map);

            var serialized = serializer.serialize(collections);

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Collections>
                        <list>
                            <value>lorem</value>
                            <value>ipsum</value>
                        </list>
                        <set>
                        </set>
                        <map>
                            <key>1</key>
                            <value>
                                <Strings>
                                    <property>one</property>
                                    <name>_</name>
                                </Strings>
                            </value>
                            <key>2</key>
                            <value>
                                <Strings>
                                    <property>two</property>
                                    <name>_</name>
                                </Strings>
                            </value>
                        </map>
                    </Collections>
                    """);
        }

        @Test
        void complex_object() {
            var serializer = new XmlSerializer();
            var complex = new Generics<>(new Generics<>(new Generics<>(42)));

            var serialized = serializer.serialize(complex);

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <Generics>
                        <t>
                            <Generics>
                                <t>
                                    <Generics>
                                        <t>42</t>
                                    </Generics>
                                </t>
                            </Generics>
                        </t>
                    </Generics>
                    """);
        }

        @Test
        @Disabled("proceeding will cause intense raging at java")
        void object_with_primitive_arrays() {
            var serializer = new XmlSerializer();

            var serialized = serializer.serialize(new PrimitiveArrays(new int[]{317, 42}, new double[0]));

            assertThat(serialized).isEqualToIgnoringWhitespace("""
                    <PrimitiveArrays>
                        <ints>
                            <value>317</value>
                            <value>42</value>
                        </ints>
                        <doubles>
                        </doubles>
                    </PrimitiveArrays>
                    """);
        }
    }
}
