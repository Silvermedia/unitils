package org.unitils.io.annotation.handler;

import org.unitils.core.TestListener;
import org.unitils.io.TemporaryFile.TemporaryFileUtil;
import org.unitils.io.annotation.TemporaryFile;
import org.unitils.io.annotation.TemporaryFolder;
import org.unitils.util.AnnotationUtils;
import org.unitils.util.ReflectionUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * * Implements the behavior of the {@link org.unitils.io.annotation.TemporaryFile} and {@link org.unitils.io.annotation.TemporaryFolder}  annotations.<br/>
 * See annotation javadoc for more info.
 *
 * @author Jeroen Horemans
 * @author Tim Ducheyne
 * @author Thomas De Rycke
 * @since 3.3
 */

public class TemporaryFileAnnotationHandler extends TestListener {

    /* The directory which unitils uses as temporary dir*/
    private File defaultRootDirectory;

    /* Boolean to */
    private Boolean defaultRemoveAfterTest;

    private TemporaryFileUtil fileUtil;

    public TemporaryFileAnnotationHandler(TemporaryFileUtil fileUtil, File defaultRootDirectory,
                                          Boolean defaultRemoveAfterTest) {
        this.fileUtil = fileUtil;
        this.defaultRootDirectory = defaultRootDirectory;
        this.defaultRemoveAfterTest = defaultRemoveAfterTest;
    }


    @Override
    public void afterTestMethod(Object testObject, Method testMethod, Throwable t) {
        Set<Field> tmpFileFields = AnnotationUtils.getFieldsAnnotatedWith(
                testObject.getClass(), TemporaryFile.class);

        for (Field field : tmpFileFields) {
            File f = ReflectionUtils.getFieldValue(testObject, field);
            fileUtil.removeTemporaryFile(f);
        }


    }

    @Override
    public void beforeTestSetUp(Object testObject, Method testMethod) {
        handleFiles(testObject, testMethod);
        handleFolders(testObject, testMethod);
    }

    private void handleFiles(Object testObject, Method testMethod) {
        Set<Field> tmpFileFields = AnnotationUtils.getFieldsAnnotatedWith(
                testObject.getClass(), TemporaryFile.class);

        for (Field field : tmpFileFields) {
            TemporaryFile annotation = field.getAnnotation(TemporaryFile.class);
            String fileName = annotation.value();
            if (fileName.isEmpty()) {
                fileName = testObject.getClass().getName() + testMethod.getName() + ".tmp";
            }
            File f = fileUtil.createTemporaryFile(fileName);
            ReflectionUtils.setFieldValue(testObject, field, f);
        }

    }


    private void handleFolders(Object testObject, Method testMethod) {
        Set<Field> tmpFileFields = AnnotationUtils.getFieldsAnnotatedWith(
                testObject.getClass(), TemporaryFolder.class);

        for (Field field : tmpFileFields) {
            TemporaryFolder annotation = field.getAnnotation(TemporaryFolder.class);
            String fileName = annotation.value();
            if (fileName.isEmpty()) {
                fileName = testObject.getClass().getName() + testMethod.getName();
            }
            File f = fileUtil.createTemporaryFolder(fileName);
            ReflectionUtils.setFieldValue(testObject, field, f);
        }

    }

}
