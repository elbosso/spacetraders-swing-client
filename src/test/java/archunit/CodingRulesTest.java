/*
 * Copyright (c) 2022-2023.
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

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import static com.tngtech.archunit.library.GeneralCodingRules.*;

@AnalyzeClasses(packages = {"de.elbosso.spacetraders.swing"})
public class CodingRulesTest
{

    private final static Logger CLASS_LOGGER =org.slf4j.LoggerFactory.getLogger(CodingRulesTest.class);

    @org.junit.Rule
    public org.junit.rules.TemporaryFolder folder = new org.junit.rules.TemporaryFolder();


    @org.junit.Rule
    public org.junit.rules.TestName name = new org.junit.rules.TestName();

    /**
     * The Before annotation indicates that this method must be executed before
     * each test in the class, so as to execute some preconditions necessary for
     * the test.
     */

    @org.junit.Before
    public void methodBefore()
    {
    }

    /**
     * The BeforeClass annotation indicates that the static method to which is
     * attached must be executed once and before all tests in the class. That
     * happens when the test methods share computationally expensive setup (e.g.
     * connect to database).
     */

    @org.junit.BeforeClass
    public static void methodBeforeClass()
    {
        de.elbosso.util.Utilities.configureBasicStdoutLogging(Level.INFO);
    }

    /**
     * The After annotation indicates that this method gets executed after
     * execution of each test (e.g. reset some variables after execution of
     * every test, delete temporary variables etc)
     */

    @org.junit.After
    public void methodAfter()
    {
    }

    /**
     * The AfterClass annotation can be used when a method needs to be executed
     * after executing all the tests in a JUnit Test Case class so as to
     * clean-up the expensive set-up (e.g disconnect from a database).
     * Attention: The method attached with this annotation (similar to
     * BeforeClass) must be defined as static.
     */

    @org.junit.AfterClass
    public static void methodAfterClass()
    {
        de.elbosso.util.Utilities.configureBasicStdoutLogging(Level.ERROR);
    }

/*    @ArchTest
    public static final ArchRule myRule = classes()
            .that().resideInAPackage("..service..")
            .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");
*/
    @ArchTest
    private final ArchRule no_access_to_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    private final ArchRule no_generic_exceptions = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    private final ArchRule no_java_util_logging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule no_inline_java_util_logging=Rules.NO_INLINE_JAVA_UTIL_LOGGING;

    @ArchTest
    private final ArchRule loggers_should_be_private_static_final =Rules.LOGGERS_SHOULD_BE_PRIVATE_STATIC_FINAL;

    @ArchTest
    private final ArchRule no_system_currenttimemillis=Rules.NO_SYSTEM_CURRENTTIMEMILLIS;

    @ArchTest
    private final ArchRule no_new_javautildate=Rules.NO_NEW_JAVAUTILDATE;

    @ArchTest
    private final ArchRule no_java_beans_introspector_getBeanInfo=
            Rules.NO_JAVA_BEANS_INTROSPECTOR_GETBEANINFO;

    @ArchTest
    private final ArchRule no_java_beans_propertyDescriptor_getDisplayName=
            Rules.NO_JAVA_BEANS_PROPERTYDESCRIPTOR_GETDISPLAYNAME;

    @ArchTest
    private final ArchRule no_java_beans_xmlEncoder_setPersistenceDelegate=
            Rules.NO_JAVA_BEANS_XMLENCODER_SETPERSISTENCEDELEGATE;

    @ArchTest
    private final ArchRule no_new_javax_swing_jfilechooser=Rules.NO_NEW_JAVAX_SWING_JFILECHOOSER;
}