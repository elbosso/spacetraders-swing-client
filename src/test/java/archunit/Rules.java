/*
 * Copyright (c) 2023.
 *
 * Juergen Key. Alle Rechte vorbehalten.
 *
 * Weiterverbreitung und Verwendung in nichtkompilierter oder kompilierter Form,
 * mit oder ohne Veraenderung, sind unter den folgenden Bedingungen zulaessig:
 *
 *    1. Weiterverbreitete nichtkompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss im Quelltext
 * enthalten.
 *    2. Weiterverbreitete kompilierte Exemplare muessen das obige Copyright,
 * die Liste der Bedingungen und den folgenden Haftungsausschluss in der
 * Dokumentation und/oder anderen Materialien, die mit dem Exemplar verbreitet
 * werden, enthalten.
 *    3. Weder der Name des Autors noch die Namen der Beitragsleistenden
 * duerfen zum Kennzeichnen oder Bewerben von Produkten, die von dieser Software
 * abgeleitet wurden, ohne spezielle vorherige schriftliche Genehmigung verwendet
 * werden.
 *
 * DIESE SOFTWARE WIRD VOM AUTOR UND DEN BEITRAGSLEISTENDEN OHNE
 * JEGLICHE SPEZIELLE ODER IMPLIZIERTE GARANTIEN ZUR VERFUEGUNG GESTELLT, DIE
 * UNTER ANDEREM EINSCHLIESSEN: DIE IMPLIZIERTE GARANTIE DER VERWENDBARKEIT DER
 * SOFTWARE FUER EINEN BESTIMMTEN ZWECK. AUF KEINEN FALL IST DER AUTOR
 * ODER DIE BEITRAGSLEISTENDEN FUER IRGENDWELCHE DIREKTEN, INDIREKTEN,
 * ZUFAELLIGEN, SPEZIELLEN, BEISPIELHAFTEN ODER FOLGENDEN SCHAEDEN (UNTER ANDEREM
 * VERSCHAFFEN VON ERSATZGUETERN ODER -DIENSTLEISTUNGEN; EINSCHRAENKUNG DER
 * NUTZUNGSFAEHIGKEIT; VERLUST VON NUTZUNGSFAEHIGKEIT; DATEN; PROFIT ODER
 * GESCHAEFTSUNTERBRECHUNG), WIE AUCH IMMER VERURSACHT UND UNTER WELCHER
 * VERPFLICHTUNG AUCH IMMER, OB IN VERTRAG, STRIKTER VERPFLICHTUNG ODER
 * UNERLAUBTE HANDLUNG (INKLUSIVE FAHRLAESSIGKEIT) VERANTWORTLICH, AUF WELCHEM
 * WEG SIE AUCH IMMER DURCH DIE BENUTZUNG DIESER SOFTWARE ENTSTANDEN SIND, SOGAR,
 * WENN SIE AUF DIE MOEGLICHKEIT EINES SOLCHEN SCHADENS HINGEWIESEN WORDEN SIND.
 *
 */

package archunit;

import com.tngtech.archunit.lang.ArchRule;
import org.slf4j.Logger;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class Rules
{
    public static final ArchRule NO_INLINE_JAVA_UTIL_LOGGING=noClasses().
            should().callMethod("java.util.logging.Logger","getLogger","java.lang.String").
            orShould().callMethod("java.util.logging.Logger","getLogger","java.lang.String","java.lang.String");

    public static final ArchRule LOGGERS_SHOULD_BE_PRIVATE_STATIC_FINAL =
//            fields().that().haveRawType(Logger.class)
            fields().that().haveName("CLASS_LOGGER")
                    .and().haveRawType(Logger.class)
                    .or().haveName("EXCEPTION_LOGGER")
                    .and().haveRawType(Logger.class)
                    .should().bePrivate()
                    .andShould().beStatic()
                    .andShould().beFinal()
                    .because("we agreed on this convention");

    public static final ArchRule NO_SYSTEM_CURRENTTIMEMILLIS=
            noClasses().
                    should().callMethod("java.lang.System","currentTimeMillis");

    public static final ArchRule NO_NEW_JAVAUTILDATE=
            noClasses().
                    should().callConstructor("java.util.Date");

    public static final ArchRule NO_JAVA_BEANS_INTROSPECTOR_GETBEANINFO=
            noClasses().
                    should().callMethod("java.beans.Introspector","getBeanInfo","java.lang.Class").
                    orShould().callMethod("java.beans.Introspector","getBeanInfo","java.lang.Class","java.lang.Class");

    public static final ArchRule NO_JAVA_BEANS_PROPERTYDESCRIPTOR_GETDISPLAYNAME=
            noClasses().
                    should().callMethod("java.beans.PropertyDescriptor","getDisplayName");

    public static final ArchRule NO_JAVA_BEANS_XMLENCODER_SETPERSISTENCEDELEGATE=
            noClasses().
                    should().callMethod("java.beans.XMLEncoder","setPersistenceDelegate","java.lang.Class","java.beans.PersistenceDelegate");


    public static final ArchRule NO_NEW_JAVAX_SWING_JFILECHOOSER=
            noClasses().
                    should().callConstructor("javax.swing.JFileChooser").
                    orShould().callConstructor("javax.swing.JFileChooser","java.lang.String").
                    orShould().callConstructor("javax.swing.JFileChooser","java.io.File").
                    orShould().callConstructor("javax.swing.JFileChooser","javax.swing.filechooser.FileSystemView").
                    orShould().callConstructor("javax.swing.JFileChooser","java.io.File","javax.swing.filechooser.FileSystemView").
                    orShould().callConstructor("javax.swing.JFileChooser","java.lang.String","javax.swing.filechooser.FileSystemView");

    private Rules()
    {
        super();
    }
}
