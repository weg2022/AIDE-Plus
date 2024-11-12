package io.github.zeroaicy.aide.completion;

import static com.android.SdkConstants.ATTR_MODULE_NAME;
import static com.android.SdkConstants.REQUEST_FOCUS;
import static com.android.SdkConstants.TAG;
import static com.android.SdkConstants.TAG_ADAPTIVE_ICON;
import static com.android.SdkConstants.TAG_ANIMATED_SELECTOR;
import static com.android.SdkConstants.TAG_ANIMATED_VECTOR;
import static com.android.SdkConstants.TAG_ANIMATION_LIST;
import static com.android.SdkConstants.TAG_APPWIDGET_PROVIDER;
import static com.android.SdkConstants.TAG_ARGUMENT;
import static com.android.SdkConstants.TAG_ARRAY;
import static com.android.SdkConstants.TAG_ATTR;
import static com.android.SdkConstants.TAG_BITMAP;
import static com.android.SdkConstants.TAG_CLIP_PATH;
import static com.android.SdkConstants.TAG_COLOR;
import static com.android.SdkConstants.TAG_DATA;
import static com.android.SdkConstants.TAG_DECLARE_STYLEABLE;
import static com.android.SdkConstants.TAG_DEEP_LINK;
import static com.android.SdkConstants.TAG_DIMEN;
import static com.android.SdkConstants.TAG_DRAWABLE;
import static com.android.SdkConstants.TAG_EAT_COMMENT;
import static com.android.SdkConstants.TAG_ENUM;
import static com.android.SdkConstants.TAG_FLAG;
import static com.android.SdkConstants.TAG_FONT;
import static com.android.SdkConstants.TAG_FONT_FAMILY;
import static com.android.SdkConstants.TAG_FRAGMENT;
import static com.android.SdkConstants.TAG_GRADIENT;
import static com.android.SdkConstants.TAG_GROUP;
import static com.android.SdkConstants.TAG_HEADER;
import static com.android.SdkConstants.TAG_IMPORT;
import static com.android.SdkConstants.TAG_INCLUDE;
import static com.android.SdkConstants.TAG_INSET;
import static com.android.SdkConstants.TAG_INTEGER_ARRAY;
import static com.android.SdkConstants.TAG_ITEM;
import static com.android.SdkConstants.TAG_LAYER_LIST;
import static com.android.SdkConstants.TAG_LAYOUT;
import static com.android.SdkConstants.TAG_LEVEL_LIST;
import static com.android.SdkConstants.TAG_MASKABLE_ICON;
import static com.android.SdkConstants.TAG_MENU;
import static com.android.SdkConstants.TAG_NAVIGATION;
import static com.android.SdkConstants.TAG_NINE_PATCH;
import static com.android.SdkConstants.TAG_PATH;
import static com.android.SdkConstants.TAG_PLURALS;
import static com.android.SdkConstants.TAG_PUBLIC;
import static com.android.SdkConstants.TAG_PUBLIC_GROUP;
import static com.android.SdkConstants.TAG_RESOURCES;
import static com.android.SdkConstants.TAG_RIPPLE;
import static com.android.SdkConstants.TAG_ROTATE;
import static com.android.SdkConstants.TAG_SELECTOR;
import static com.android.SdkConstants.TAG_SHAPE;
import static com.android.SdkConstants.TAG_SKIP;
import static com.android.SdkConstants.TAG_STAGING_PUBLIC_GROUP;
import static com.android.SdkConstants.TAG_STAGING_PUBLIC_GROUP_FINAL;
import static com.android.SdkConstants.TAG_STRING;
import static com.android.SdkConstants.TAG_STRING_ARRAY;
import static com.android.SdkConstants.TAG_STYLE;
import static com.android.SdkConstants.TAG_TRANSITION;
import static com.android.SdkConstants.TAG_VARIABLE;
import static com.android.SdkConstants.TAG_VECTOR;
import static com.android.SdkConstants.VIEW_FRAGMENT;
import static com.android.SdkConstants.VIEW_INCLUDE;
import static com.android.SdkConstants.VIEW_MERGE;
import static com.android.SdkConstants.VIEW_PKG_PREFIX;
import static com.android.SdkConstants.VIEW_TAG;
import static com.android.SdkConstants.WIDGET_PKG_PREFIX;
import static com.android.aapt.Resources.Attribute.FormatFlags.BOOLEAN;
import static com.android.aapt.Resources.Attribute.FormatFlags.COLOR;
import static com.android.aapt.Resources.Attribute.FormatFlags.DIMENSION;
import static com.android.aapt.Resources.Attribute.FormatFlags.ENUM;
import static com.android.aapt.Resources.Attribute.FormatFlags.FLAGS;
import static com.android.aapt.Resources.Attribute.FormatFlags.INTEGER;
import static com.android.aapt.Resources.Attribute.FormatFlags.REFERENCE;
import static com.android.aapt.Resources.Attribute.FormatFlags.STRING;
import static com.android.aaptcompiler.AaptResourceType.ATTR;
import static com.android.aaptcompiler.AaptResourceType.BOOL;
import static com.android.aaptcompiler.AaptResourceType.DIMEN;
import static com.android.aaptcompiler.AaptResourceType.STYLEABLE;
import static io.github.zeroaicy.aide.aaptcompiler.ResourceUtilsKt.PCK_ANDROID;

import android.content.Context;
import android.util.Pair;

import com.aide.codemodel.api.ClassType;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.IdentifierSpace;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.Namespace;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.collections.ListOf;
import com.aide.codemodel.api.collections.MapOfInt;
import com.aide.common.AppLog;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.project.WearAppProjectSupport;
import com.aide.ui.util.FileSystem;
import com.android.SdkConstants;
import com.android.aapt.Resources;
import com.android.aaptcompiler.AaptResourceType;
import com.android.aaptcompiler.AttributeResource;
import com.android.aaptcompiler.ConfigDescription;
import com.android.aaptcompiler.Reference;
import com.android.aaptcompiler.ResourceConfigValue;
import com.android.aaptcompiler.ResourceEntry;
import com.android.aaptcompiler.ResourceGroup;
import com.android.aaptcompiler.ResourceTable;
import com.android.aaptcompiler.ResourceTablePackage;
import com.android.aaptcompiler.Styleable;

import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import io.github.zeroaicy.aide.aaptcompiler.ApiVersionsUtils;
import io.github.zeroaicy.aide.aaptcompiler.JavaViewUtils;
import io.github.zeroaicy.aide.aaptcompiler.ResourceUtils;
import io.github.zeroaicy.aide.aaptcompiler.WidgetTableUtils;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.Widget;
import io.github.zeroaicy.aide.aaptcompiler.interfaces.widgets.WidgetTable;
import io.github.zeroaicy.aide.aaptcompiler.permissions.Permission;
import io.github.zeroaicy.aide.aaptcompiler.utils.PatternsKt;
import io.github.zeroaicy.aide.aaptcompiler.utils.StyleUtils;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.Log;
import java.util.stream.Stream;


public class XmlCompletionUtils {


    /// Tags: Resources
    public static final String[] RESOURCES_TAGS = {
            TAG_RESOURCES,
            TAG_STRING,
            TAG_ARRAY,
            TAG_STYLE,
            TAG_ITEM,
            TAG_GROUP,
            TAG_STRING_ARRAY,
            TAG_PLURALS,
            TAG_INTEGER_ARRAY,
            TAG_COLOR,
            TAG_DIMEN,
            TAG_DRAWABLE,
            TAG_MENU,
            TAG_ENUM,
            TAG_FLAG,
            TAG_ATTR,
            TAG_DECLARE_STYLEABLE,
            TAG_EAT_COMMENT,
            TAG_SKIP,
            TAG_PUBLIC,
            TAG_PUBLIC_GROUP,
            TAG_STAGING_PUBLIC_GROUP,
            TAG_STAGING_PUBLIC_GROUP_FINAL
    };
    /// Tags: Layouts
    public static final String[] LAYOUTS_TAGS = {
            VIEW_TAG,
            VIEW_INCLUDE,
            VIEW_MERGE,
            VIEW_FRAGMENT,
            REQUEST_FOCUS,
            TAG
    };
    /// Tags: Navigation
    public static final String[] NAVIGATION_TAGS = {
            TAG_INCLUDE,
            TAG_DEEP_LINK,
            TAG_NAVIGATION,
            TAG_FRAGMENT,
            TAG_ARGUMENT,
            ATTR_MODULE_NAME
    };
    /// Tags: Drawables
    public static final String[] DRAWABLE_TAGS = {
            TAG_ANIMATION_LIST,
            TAG_ANIMATED_SELECTOR,
            TAG_ANIMATED_VECTOR,
            TAG_BITMAP,
            TAG_CLIP_PATH,
            TAG_GRADIENT,
            TAG_INSET,
            TAG_LAYER_LIST,
            TAG_NINE_PATCH,
            TAG_PATH,
            TAG_RIPPLE,
            TAG_ROTATE,
            TAG_SHAPE,
            TAG_SELECTOR,
            TAG_TRANSITION,
            TAG_VECTOR,
            TAG_LEVEL_LIST
    };
    /// Tags: Data-Binding
    public static final String[] DATA_BINDING_TAGS = {
            TAG_LAYOUT,
            TAG_DATA,
            TAG_VARIABLE,
            TAG_IMPORT
    };
    /// Tags: XML
    public static final String[] XML_TAGS = {
            TAG_HEADER,
            TAG_APPWIDGET_PROVIDER
    };
    /// Tags: MENU
    public static final String[] MENU_TAGS = {
            TAG_ITEM,
            TAG_GROUP,
            TAG_MENU
    };
    /// Font family tag
    public static final String[] FONT_TAGS = {
            TAG_FONT_FAMILY,
            TAG_FONT
    };
    /// Tags: Adaptive icon
    public static final String[] ADAPTIVE_ICON_TAGS = {
            TAG_ADAPTIVE_ICON,
            TAG_MASKABLE_ICON
    };
    /// Tags: anim
    public static final String[] ANIMATION_TAGS = new String[]{
            "translate",
            "scale",
            "rotate",
            "alpha",
            "set"
    };
    /// Tags: animator
    public static final String[] ANIMATOR_TAGS = {
            "objectAnimator",
            "animator",
            "propertyValuesHolder",
            "set"
    };
    /// Tags: colors
    public static final String[] COLORS_TAGS = {
            TAG_SELECTOR,
            TAG_GRADIENT,
            TAG_ITEM
    };
    private static final String MANIFEST_TAG_PREFIX = "AndroidManifest";
    private static final String NAMESPACE_PREFIX = "http://schemas.android.com/apk/res/";
    private static final String NAMESPACE_AUTO = "http://schemas.android.com/apk/res-auto";
    private static final String NAMESPACE_TOOLS = "http://schemas.android.com/tools";
    private static final String NAMESPACE_AUTO_NAME = "app";
    private static final String NAMESPACE_PREFIX_NAME = "android";
    private static final String NAMESPACE_TOOLS_NAME = "tools";
    /// dimensionUnits
    private static final String[] DIMENSION_UNITS = {
            SdkConstants.UNIT_DP,
            SdkConstants.UNIT_SP,
            SdkConstants.UNIT_PX,
            SdkConstants.UNIT_IN,
            SdkConstants.UNIT_MM,
            SdkConstants.UNIT_PT
    };
    private static ResourceUtils resourceUtil;
    private static ApiVersionsUtils apiVersionsUtil;
    private static WidgetTableUtils widgetTableUtil;
    private static ListOf<Member> mEntitySpace;
    private static JavaViewUtils javaViewUtils;

    public static File getPlatformDir() {
        return new File(FileSystem.getNoBackupFilesDirPath(), ".aide/sdk");
    }

    public static void initAndroidSDK() {
        try {
            var now = System.currentTimeMillis();
            Context ctx = ContextUtil.getContext();
            var platformDir = getPlatformDir();
            if (platformDir.exists() && platformDir.isDirectory()) {
                initAndroidSdkData();
                Log.i(XmlCompletionUtils.class.getSimpleName(), "platformDir exists");
            } else {
                FileSystem.unZip(ctx.getAssets().open("data.zip"), Objects.requireNonNull(platformDir.getParentFile()).getAbsolutePath(), true);
                initAndroidSdkData();
            }
            AppLog.d("解压耗时", (System.currentTimeMillis() - now) + "ms");
        } catch (IOException e) {
            e.printStackTrace();
            AppLog.e("initAndroidSDK", e.getMessage(), e);
        }
    }

    private static void initAndroidSdkData() {
        resourceUtil = ResourceUtils.getInstance(getPlatformDir());
        apiVersionsUtil = ApiVersionsUtils.getInstance(getPlatformDir());
        widgetTableUtil = WidgetTableUtils.getInstance(getPlatformDir());
        javaViewUtils = JavaViewUtils.getInstance();
    }

    /// 已完成 tag 补全
    public static void completionTag(final Model model, SyntaxTree syntaxTree, int line, int column) {
        model.codeCompleterCallback.listStarted();
        // 提示节点
        String fileName = getParentName(syntaxTree);
        FileEntry file = syntaxTree.getFile();
        Log.d("fileName", syntaxTree.getFile().getFullNameString());
        if (file.getFullNameString().equals("AndroidManifest.xml")) {
            XmlCompletionUtils.completionManifestTag(model);
        } else if (fileName.startsWith("layout")) {
            final var androidNamespace = model.entitySpace.getRootNamespace().getMemberNamespace(model.identifierSpace.get("android"));
            JavaViewUtils.Companion.getJavaViewClasses().forEach(new BiConsumer<String, JavaClass>() {
                @Override
                public void accept(String s, JavaClass javaClass) {
                    Namespace memberNamespace;
                    String className;
                    if (s.startsWith(WIDGET_PKG_PREFIX)) {
                        className = StyleUtils.getSimpleName(s);
                        memberNamespace = androidNamespace.getMemberNamespace(model.identifierSpace.get("widget")).getMemberNamespace(model.identifierSpace.get(className));
                    } else if (s.startsWith(VIEW_PKG_PREFIX)) {
                        className = StyleUtils.getSimpleName(s);
                        memberNamespace = androidNamespace.getMemberNamespace(model.identifierSpace.get("view")).getMemberNamespace(model.identifierSpace.get(className));
                    } else {
                        className = s;
                        memberNamespace = getSimpleClass(s, model);
                    }
                    model.codeCompleterCallback.aM(memberNamespace, className);
                }
            });
            codeCompletion(LAYOUTS_TAGS, model);
            codeCompletion(DATA_BINDING_TAGS, model);
        } else if (fileName.startsWith("xml")) {
            codeCompletion(XML_TAGS, model);
        } else if (fileName.startsWith("values")) {
            Arrays.stream(RESOURCES_TAGS).forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    String prefix;
                    Namespace memberNamespace = model.entitySpace.getRootNamespace().getMemberNamespace(model.identifierSpace.get(s));
                    if (s.equals(TAG_RESOURCES)) {
                        prefix = s + ">\\n\\t|\\n</" + s + ">";
                    } else {
                        prefix = s + " name=\\\"|\\\"></" + s + ">";
                    }
                    model.codeCompleterCallback.aM(memberNamespace, prefix);
                }
            });
        } else if (fileName.startsWith("menu")) {
            codeCompletion(MENU_TAGS, model);
        } else if (fileName.startsWith("anim")) {
            codeCompletion(ANIMATION_TAGS, model);
        } else if (fileName.startsWith("color")) {
            codeCompletion(COLORS_TAGS, model);
        } else if (fileName.startsWith("font")) {
            codeCompletion(FONT_TAGS, model);
        } else if (fileName.startsWith("animator")) {
            codeCompletion(ANIMATOR_TAGS, model);
        } else if (fileName.startsWith("navigation")) {
            codeCompletion(NAVIGATION_TAGS, model);
        } else if (fileName.startsWith("drawable")) {
            codeCompletion(DRAWABLE_TAGS, model);
            codeCompletion(ADAPTIVE_ICON_TAGS, model);
        }
    }

    /// 已完成 attr 补全
    public static void completionAttrs(Model model, SyntaxTree syntaxTree, int tag, int element) {

        String parentTag = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(syntaxTree.getParentNode(element), 1), 2));
        String parentName = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(syntaxTree.getParentNode(element), 1), 2));


        Namespace rootNamespace = model.entitySpace.getRootNamespace();
        /// 补全 xmlns namespace
        HashMap<String, String> nameSpaces = new HashMap<>();
        nameSpaces.put(SdkConstants.ANDROID_NS_NAME, SdkConstants.ANDROID_URI);
        nameSpaces.put(SdkConstants.APP_PREFIX, SdkConstants.AUTO_URI);
        nameSpaces.put(SdkConstants.TOOLS_PREFIX, SdkConstants.TOOLS_URI);
        for (Map.Entry<String, String> entry : nameSpaces.entrySet()) {
            String formattedNamespace = String.format("%s%s=\"%s\"", SdkConstants.XMLNS_PREFIX, entry.getKey(), entry.getValue());
            model.codeCompleterCallback.aM(rootNamespace.getMemberNamespace(model.identifierSpace.get(entry.getKey())), formattedNamespace);
        }


        for (Map.Entry<String, String> entry : nameSpaces.entrySet()) {
            Set<ResourceTable> tables = findResourceTables(entry.getValue());
            if (tables.isEmpty()) {
                continue;
            }
            var namespace = entry.getValue();
            String pck = namespace.contains(SdkConstants.URI_PREFIX)
                    ? namespace.substring(namespace.indexOf(SdkConstants.URI_PREFIX) + SdkConstants.URI_PREFIX.length())
                    : namespace;
            Set<ResourceTablePackage> packages = new HashSet<>();
            for (ResourceTable table : tables) {
                if (NAMESPACE_AUTO.equals(entry.getValue())) {
                    for (ResourceTablePackage tablePackage : table.getPackages()) {
                        if (!tablePackage.getName().isBlank()) {
                            packages.add(tablePackage);
                        }
                    }
                } else {
                    ResourceTablePackage tablePackage = table.findPackage(pck);
                    if (tablePackage != null) {
                        packages.add(tablePackage);
                    }
                }
            }
            for (ResourceTablePackage tablePackage : packages) {
                ResourceGroup styleables = tablePackage.findGroup(STYLEABLE, null);
                if (styleables == null) {
                    return;
                }
                Set<Styleable> nodeStyleables = findNodeStyleables(parentTag, parentName, styleables);
                for (Styleable nodeStyleable : nodeStyleables) {
                    for (Reference ref : nodeStyleable.getEntries()) {
                        String vorName = SdkConstants.ANDROID_NS_NAME;
                        if (pck.equals(SdkConstants.ANDROID_NS_NAME)) {
                            vorName = SdkConstants.ANDROID_NS_NAME;
                        } else if (pck.equals(SdkConstants.AUTO_URI)) {
                            vorName = SdkConstants.APP_PREFIX;
                        } else if (pck.equals(SdkConstants.TOOLS_URI)) {
                            vorName = SdkConstants.TOOLS_NS_NAME;
                        }
                        String attrName = String.format("%s:%s=\"|\"", vorName, ref.getName().getEntry());
                        model.codeCompleterCallback.aM(rootNamespace.getMemberNamespace(model.identifierSpace.get(ref.getName().getEntry())), attrName);
                    }
                }


            }


        }


    }

    /// 已完成 value 补全
    public static void completionValue(Model model, SyntaxTree syntaxTree, int property, int index) {
        /// text 即 ‘android:label’ 前面的  ‘label’
        String text = model.identifierSpace.getString(property);
        /// ns 即 ‘android:label’ 前面的  ‘android’
        String ns = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 0), 0));
        /// parent 即 <application 中的 application
        String parent = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(syntaxTree.getParentNode(index), 1), 2));
        /// 已经输入的用来判断是否是以 @ 开头
        String prefix = syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1));

        /// 补全name属性
        if ("name".equals(text) && "android".equals(ns)) {
            if (parent.equals("action")) {
                completeActions(model, syntaxTree, property);
            } else if (parent.equals("category")) {
                for (String category : resourceUtil.getCategories()) {
                    model.codeCompleterCallback.listElementKeywordFound(category);
                }
            } else if (parent.equals("uses-permission")) {
                for (var permission : Permission.values()) {
                    model.codeCompleterCallback.listElementKeywordFound(permission.getConstant());
                }
            } else if (parent.equals("uses-feature")) {
                for (String feature : resourceUtil.getFeatures()) {
                    model.codeCompleterCallback.listElementKeywordFound(feature);
                }
            } else if ("activity".equals(parent)) {
                addCompletionSuperClassSuper(syntaxTree, model, syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1)), true, "app", "Activity");
            } else if ("service".equals(parent)) {
                addCompletionSuperClassSuper(syntaxTree, model, syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1)), true, "app", "Service");
            } else if ("application".equals(parent)) {
                addCompletionSuperClassSuper(syntaxTree, model, syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1)), true, "app", "Application");
            } else if ("provider".equals(parent)) {
                addCompletionSuperClassSuper(syntaxTree, model, syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1)), true, "content", "BroadcastReceiver");
            } else if ("receiver".equals(parent)) {
                addCompletionSuperClassSuper(syntaxTree, model, syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 2), 1)), true, "content", "ContentProvider");
            }
        }

        String namespace = SdkConstants.URI_PREFIX;
        if (ns.equals(SdkConstants.ANDROID_NS_NAME)) {
            namespace = SdkConstants.ANDROID_URI;
        } else if (ns.equals(SdkConstants.APP_PREFIX)) {
            namespace = SdkConstants.AUTO_URI;
        } else if (ns.equals(SdkConstants.TOOLS_PREFIX)) {
            namespace = SdkConstants.TOOLS_URI;
        }

        var tables = findResourceTables(namespace);

        var attr = findAttr(tables, namespace, ns, text);

        if (!prefix.startsWith("@")) {
            addValuesForAttr(attr, ns, text, model);
            return;
        }


        // If user is typign entry with package name and resource type. For example
        // '@com.itsaky.test.app:string/app_name' or '@android:string/ok'
        Matcher matcher = PatternsKt.getAttrValue_qualifiedRef().matcher(prefix);
        if (matcher.matches()) {
            final String valPck = matcher.group(1);
            String typeStr = matcher.group(3);
            AaptResourceType valType = null;
            for (AaptResourceType type : AaptResourceType.values()) {
                if (type.getTagName().equals(typeStr)) {
                    valType = type;
                    break;
                }
            }
            if (valType == null) {
                return; // If valType is null, return early
            }
            String newPrefix = matcher.group(4) != null ? matcher.group(4) : "";

            addValues(valType, newPrefix, new Function<String, Boolean>() {
                @Override
                public Boolean apply(String s) {
                    return s.equals(valPck);
                }
            }, model);
            return;
        }

        // If user is typing qualified reference but with incomplete type
        // For example: '@android:str' or '@com.itsaky.test.app:str'
        matcher = PatternsKt.getAttrValue_qualifiedRefWithIncompleteType().matcher(prefix);
        if (matcher.matches()) {
            String valPck = matcher.group(1); // Package name
            String incompleteType = matcher.group(3) != null ? matcher.group(3) : "";

            addResourceTypes(valPck, model);
            return; // Early return to exit the function
        }

        // If user is typing qualified reference but with incomplete type or package name
        // For example: '@android:str' or '@str'
        matcher = PatternsKt.getAttrValue_qualifiedRefWithIncompletePckOrType().matcher(prefix);
        if (matcher.matches()) {
            String valPck = matcher.group(1);
            if (valPck != null && !valPck.contains(".")) {
                addResourceTypes("", model);
            }
            addPackages(model);
            return;
        }

        // If user is typing entry name with resource type. For example '@string/app_name'
        matcher = PatternsKt.getAttrValue_unqualifiedRef().matcher(prefix);
        if (matcher.matches()) {
            final String typeStr = matcher.group(1);
            String newPrefix = matcher.group(2) != null ? matcher.group(2) : "";
            AaptResourceType valType = Arrays.stream(AaptResourceType.values())
				.filter(new Predicate<AaptResourceType>(){

					@Override
					public boolean test(AaptResourceType type) {
						return type.getTagName().equals(typeStr);
					}
				})
                    .findFirst()
                    .orElse(null);
            if (valType == null) {
                return; // No match found, exit
            }
            addValues(valType, newPrefix,model);
        }








    }




    private static void addPackages(Model model) {
        List<ResourceTablePackage> packages = findResourceTables(SdkConstants.ANDROID_URI).stream()
			.flatMap(new Function<ResourceTable, Stream<ResourceTablePackage>>(){

				@Override
				public Stream<ResourceTablePackage> apply(ResourceTable table) {
					return table.getPackages().stream();
				}
				
					
				}) // 展平所有包
                .collect(Collectors.<ResourceTablePackage>toList());
        for (ResourceTablePackage pck : packages) {
            createEnumOrFlagCompletionItem(pck.getName(), pck.getName(), model);
        }
    }

    private static void addResourceTypes(String pck, Model model) {
        for (String resType : listResTypes()) {
            createEnumOrFlagCompletionItem(pck, resType, model);
        }
    }

    private static List<String> listResTypes() {
        List<String> resTypes = new ArrayList<>();
        for (AaptResourceType type : AaptResourceType.values()) {
            resTypes.add(type.getTagName());
        }
        return resTypes;
    }


    public static AttributeResource findAttr(
            Set<ResourceTable> tables,
            String namespace,
            String pck,
            String attr
    ) {
        if (!namespace.equals(SdkConstants.AUTO_URI) && pck.equals(SdkConstants.ANDROID_NS_NAME)) {
            // AndroidX dependencies include attribute declarations with the 'android' package
            // Those must not be included when completing values
            ResourceTablePackage frameworkPackage = ResourceUtils.getCOMPLETION_FRAMEWORK_RES() != null
                    ? ResourceUtils.getCOMPLETION_FRAMEWORK_RES().findPackage(SdkConstants.ANDROID_NS_NAME)
                    : null;

            ResourceGroup attrGroup = frameworkPackage != null
                    ? frameworkPackage.findGroup(ATTR, null)
                    : null;

            ResourceEntry attrEntry = attrGroup != null
                    ? attrGroup.findEntry(attr, null)
                    : null;

            ResourceConfigValue resourceItem = attrEntry != null
                    ? attrEntry.findValue(new ConfigDescription(), "")
                    : null;
            var _attrEntry = resourceItem != null ? resourceItem.getValue() : null;
            if (_attrEntry instanceof AttributeResource) {
                return (AttributeResource) _attrEntry;
            } else {
                return null;
            }
        }

        if (namespace.equals(SdkConstants.AUTO_URI)) {
            var allPackages = new ArrayList<ResourceTablePackage>();
            for (ResourceTable table : tables) {
                allPackages.addAll(table.getPackages());
            }
            return findAttr(allPackages, attr);
        } else {
            var filteredPackages = new ArrayList<ResourceTablePackage>();
            for (ResourceTable table : tables) {
                ResourceTablePackage foundPackage = table.findPackage(pck);
                if (foundPackage != null) {
                    filteredPackages.add(foundPackage);
                }
            }
            return findAttr(filteredPackages, attr);
        }
    }

    private static AttributeResource findAttr(
            List<ResourceTablePackage> packages,
            String attr
    ) {
        for (ResourceTablePackage pck : packages) {
            ResourceGroup attrGroup = pck.findGroup(ATTR, null);
            if (attrGroup == null) {
                continue;
            }
            ResourceEntry entry = attrGroup.findEntry(attr, null);
            if (entry == null) {
                continue;
            }

            var resourceItem = entry.findValue(new ConfigDescription(), "");
            var _attrEntry = resourceItem != null ? resourceItem.getValue() : null;
            if (_attrEntry instanceof AttributeResource) {
                return (AttributeResource) _attrEntry;
            }
        }
        return null;
    }

    private static void addValuesForAttr(
            AttributeResource attr,
            String pck,
            String prefix,
            Model model
    ) {
        if (attr.getTypeMask() == Resources.Attribute.FormatFlags.REFERENCE_VALUE) {
            completeReferences(prefix, model);
        } else {
            // 检查特定属性格式
            if (hasType(attr, STRING)) {
                addValues(AaptResourceType.STRING, prefix, model);
            }

            if (hasType(attr, INTEGER)) {
                addValues(AaptResourceType.INTEGER, prefix, model);
            }

            if (hasType(attr, COLOR)) {
                addValues(AaptResourceType.COLOR, prefix, model);
            }

            if (hasType(attr, BOOLEAN)) {
                addValues(BOOL, prefix, model);
            }

            if (hasType(attr, DIMENSION)) {
                if (!prefix.isEmpty() && Character.isDigit(prefix.charAt(0))) {
                    addConstantDimensionValues(prefix, model);
                } else {
                    addValues(DIMEN, prefix, model);
                }
            }

            if (hasType(attr, INTEGER)) {
                addValues(AaptResourceType.INTEGER, prefix, model);
            }

            if (hasType(attr, ENUM) || hasType(attr, FLAGS)) {
                for (AttributeResource.Symbol symbol : attr.getSymbols()) {
                    createEnumOrFlagCompletionItem(pck, symbol.getSymbol().getName().getEntry(), model);
                }
            }

            if (hasType(attr, REFERENCE)) {
                completeReferences(prefix, model);
            }
        }
    }

    private static void createEnumOrFlagCompletionItem(String entry, Model model) {
        model.codeCompleterCallback.listElementKeywordFound(entry);
    }

    private static void createEnumOrFlagCompletionItem(String pck, String entry, Model model) {
        model.codeCompleterCallback.listElementKeywordFound(entry);
    }

    private static void addConstantDimensionValues(String prefix, Model model) {
        int i = 0;
        while (i < prefix.length() && Character.isDigit(prefix.charAt(i))) {
            ++i;
        }
        String dimen = prefix.substring(0, i);
        for (String unit : DIMENSION_UNITS) {
            String value = dimen + unit;
            createEnumOrFlagCompletionItem(value, model);
        }
    }


    private static void completeReferences(String prefix, Model model) {
        for (AaptResourceType value : AaptResourceType.getEntries()) {
            if (value == AaptResourceType.UNKNOWN) {
                continue;
            }
            addValues(value, prefix, model);
        }
    }

    private static void addValues(
            AaptResourceType type,
            String prefix,
            Function<String, Boolean> checkPck,
            Model model
    ) {
        if (checkPck == null) {
            checkPck = new Function<String, Boolean>() {
                @Override
                public Boolean apply(String s) {
                    return true; // 默认函数
                }
            };
        }
        /// 直接添加所有的 ns
        Set<Pair<String, String>> allNamespaces = new HashSet<>();
        allNamespaces.add(new Pair<String, String>(SdkConstants.ANDROID_NS_NAME, SdkConstants.ANDROID_URI));
        allNamespaces.add(new Pair<String, String>(SdkConstants.APP_PREFIX, SdkConstants.AUTO_URI));
        allNamespaces.add(new Pair<String, String>(SdkConstants.TOOLS_NS_NAME, SdkConstants.TOOLS_URI));
        Set<Pair<String, List<ResourceEntry>>> entries = new HashSet<>();

        for (Pair<String, String> pair : allNamespaces) {
            Set<ResourceTable> tables = findResourceTables(pair.second);

            // 遍历资源表
            for (ResourceTable table : tables) {
                List<ResourceTablePackage> packages = table.getPackages();

                // 遍历包
                for (ResourceTablePackage pck : packages) {
                    if (!checkPck.apply(pck.getName())) {
                        continue; // 如果包名不符合条件，则跳过
                    }

                    // 获取 styleable 资源
                    ResourceGroup group = pck.findGroup(type, null);
                    if (group != null) {
                        var foundEntries = findEntries(group.getEntries$aaptcompiler_release(), null, new Predicate<String>() {
                            @Override
                            public boolean test(String s) {
                                return true;
                            }
                        });
                        if (!foundEntries.isEmpty()) {
                            entries.add(new Pair<String, List<ResourceEntry>>(pck.getName(), foundEntries));
                        }
                    }
                }
            }
        }

        for (Pair<String, List<ResourceEntry>> pair : entries) {
            List<ResourceEntry> entryList = pair.second;
            if (entryList != null) {
                for (ResourceEntry entry : entryList) {

                    createAttrValueCompletionItem(pair.first, type.getTagName(), entry.getName(), model);
                }
            }
        }
    }

    private static void createAttrValueCompletionItem(
            String pck,
            String type,
            String name,
            Model model
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        if (pck != null && pck.equals(SdkConstants.ANDROID_NS_NAME)) {
            sb.append(pck);
            sb.append(":");
        }
        sb.append(type);
        sb.append("/");
        sb.append(name);
        model.codeCompleterCallback.listElementKeywordFound(sb.toString());
    }

    private static void addValues(
            AaptResourceType type,
            String prefix,
            Model model
    ) {
        addValues(type, prefix, new Function<String, Boolean>() {
            @Override
            public Boolean apply(String s) {
                return true; // 默认函数
            }
        }, model);
    }

    private static String getParentName(SyntaxTree syntaxTree) {
        return syntaxTree.getFile().getParentDirectory().getFullNameString();
    }

    public static void completionManifestTag(final Model model) {
        var completionManifestAttrRes = ResourceUtils.getCOMPLETION_MANIFEST_ATTR_RES();
        if (completionManifestAttrRes == null)
            completionManifestAttrRes = resourceUtil.getManifestAttrTable();
        var styleables = completionManifestAttrRes.findPackage(PCK_ANDROID)
                .findGroup(STYLEABLE, null);

        if (styleables != null) {
            findEntries(styleables.getEntries$aaptcompiler_release(), null, new Predicate<String>() {
                @Override
                public boolean test(String s) {
                    return s.startsWith(MANIFEST_TAG_PREFIX);
                }
            }).stream().map(new Function<ResourceEntry, String>() {
                @Override
                public String apply(ResourceEntry resourceEntry) {
                    return transformToTagName(resourceEntry.getName(), MANIFEST_TAG_PREFIX);
                }
            }).forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    model.codeCompleterCallback.listElementKeywordFound(s);
                }
            });
        }


    }


    public static void addCompletionSuperClassSuper(SyntaxTree syntaxTree, Model model, String str, boolean z, String str2, String str3) {
        String takePackageName = takePackageName(syntaxTree);
        IdentifierSpace identifierSpace = model.identifierSpace;
        ClassType classType = model.entitySpace.getRootNamespace().getMemberNamespace(identifierSpace.get("android")).getMemberNamespace(identifierSpace.get(str2)).getAllMemberClassTypes().get(identifierSpace.get(str3));
        MapOfInt<ClassType>.Iterator createIterator = model.entitySpace.XG().createIterator();
        int lastIndexOf = str.lastIndexOf(46);
        if (-1 == lastIndexOf) {
            int length = takePackageName.length();
            while (createIterator.hasMoreElements()) {
                ClassType classType2 = createIterator.nextValue();
                if (z || classType2.getDeclarationNumber() != 0) {
                    if ((classType2.getModifiers() & (-129)) == 1 && classType2 != classType && classType2.getIdentifierString().contains(str3)) {
                        try {
                            if (classType2.isSubClassTypeOf(classType)) {
                                String fullyQualifiedNameString = classType2.getFullyQualifiedNameString();
                                if (length > 0 && fullyQualifiedNameString.length() > length && '.' == fullyQualifiedNameString.charAt(length) && fullyQualifiedNameString.startsWith(takePackageName)) {
                                    fullyQualifiedNameString = fullyQualifiedNameString.substring(length);
                                }
                                model.codeCompleterCallback.aM(classType2, fullyQualifiedNameString);
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
            return;
        }
        if (lastIndexOf > 0) {
            if ('.' == str.charAt(0)) {
                takePackageName = takePackageName + str.substring(0, lastIndexOf);
            } else {
                takePackageName = str.substring(0, lastIndexOf);
            }
        }
        int length2 = takePackageName.length();
        while (createIterator.hasMoreElements()) {
            ClassType classType3 = createIterator.nextValue();
            if (z || classType3.getDeclarationNumber() != 0) {
                if ((classType3.getModifiers() & (-129)) == 1 && classType3.getIdentifierString().contains(str3)) {
                    String fullyQualifiedNameString2 = classType3.getFullyQualifiedNameString();
                    if (fullyQualifiedNameString2.length() > length2 && '.' == fullyQualifiedNameString2.charAt(length2) && fullyQualifiedNameString2.startsWith(takePackageName)) {
                        try {
                            if (classType3.isSubClassTypeOf(classType)) {
                                model.codeCompleterCallback.aM(classType3, fullyQualifiedNameString2.substring(length2 + 1));
                            }
                        } catch (Throwable ignored) {
                        }
                    }
                }
            }
        }
    }

    /**
     * 补全action的name内容
     */
    private static void completeActions(Model model, SyntaxTree syntaxTree, int property) {
        List<String> actionsList = List.of();
        String parentParent = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(syntaxTree.getChildNode(syntaxTree.getParentNode(property), 1), 2), 3));
        if (parentParent.equals(SdkConstants.TAG_INTENT_FILTER)) {
            actionsList = resourceUtil.getActivityActions();
        } else if (parentParent.equals(SdkConstants.TAG_RECEIVER)) {
            actionsList = resourceUtil.getBroadcastActions();
        } else if (parentParent.equals(SdkConstants.TAG_SERVICE)) {
            actionsList = resourceUtil.getServiceActions();
        }
        String text = model.identifierSpace.getString(property);
        String ns = syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(property, 0), 0));
        if ("name".equals(text) && "android".equals(ns)) {
            for (String action : actionsList) {
                model.codeCompleterCallback.listElementKeywordFound(action);
            }
        }
    }

    public static Set<ResourceTable> findAllModuleResourceTables() {
        Set<ResourceTable> sourceResTables = ResourceUtils.getCOMPLETION_MODULE_RES();
        return new HashSet<>(sourceResTables);
    }

    public static Set<ResourceTable> findResourceTables(String nsUri) {
        if (nsUri == null || nsUri.trim().isEmpty()) {
            return Collections.emptySet();
        }
        if (SdkConstants.AUTO_URI.equals(nsUri)) {
            return findAllModuleResourceTables();
        }
        String pck = nsUri.startsWith(SdkConstants.URI_PREFIX) ? nsUri.substring(SdkConstants.URI_PREFIX.length()) : "";
        if (pck.isEmpty()) {
            return Collections.emptySet();
        }
        if (SdkConstants.ANDROID_NS_NAME.equals(pck)) {
            var completionFrameworkRes = ResourceUtils.getCOMPLETION_FRAMEWORK_RES();
            if (completionFrameworkRes == null)
                completionFrameworkRes = resourceUtil.getFrameworkResourceTable();
            if (completionFrameworkRes == null) {
                return Collections.emptySet();
            }
            return Collections.singleton(completionFrameworkRes);
        }
        ResourceTable table = resourceUtil.forPackage(pck);
        if (table == null) {
            return Collections.emptySet();
        }
        return Collections.singleton(table);
    }


    /**
     * 提取最顶部的tag名字
     */
    public static String takeRootName(SyntaxTree syntaxTree) {
        int childNode = syntaxTree.getChildNode(syntaxTree.getRootNode(), 0);
        int childCount = syntaxTree.getChildCount(childNode);
        for (int i = 0; i < childCount; i++) {
            int childNode2 = syntaxTree.getChildNode(childNode, i);
            if (syntaxTree.isBlockNode(childNode2)) {
                int childNode3 = syntaxTree.getChildNode(childNode2, 0);
                int childCount2 = syntaxTree.getChildCount(childNode3);
                for (int i2 = 0; i2 < childCount2; i2++) {
                    int childNode4 = syntaxTree.getChildNode(childNode3, i2);
                    if (217 == syntaxTree.getSyntaxTag(childNode4)) {
                        int childNode5 = syntaxTree.getChildNode(childNode4, 0);
                        if (syntaxTree.isIdentifierNode(childNode5) && syntaxTree.getIdentifierString(childNode5).isEmpty()) {
                            return syntaxTree.getIdentifierString(syntaxTree.getChildNode(childNode4, 2));
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 提取所有的R类
     */
    private static List<ClassType> takeResourceClasses(Model model) {
        List<ClassType> arrayList = new ArrayList<>();
        int i = model.identifierSpace.get("R");
        MapOfInt<ClassType>.Iterator createIterator = model.entitySpace.XG().createIterator();
        while (createIterator.hasMoreElements()) {
            ClassType classType = createIterator.nextValue();
            if (i == classType.getIdentifier()) {
                arrayList.add(classType);
            }
        }
        return arrayList;
    }

    /**
     * 提取包名
     */
    private static String takePackageName(SyntaxTree syntaxTree) {
        FileEntry parentDirectory = syntaxTree.getFile().getParentDirectory();
        if (AndroidProjectSupport.isAndroidGradleProject(parentDirectory.getPathString())) {
            int childNode = syntaxTree.getChildNode(syntaxTree.getRootNode(), 0);
            int childCount = syntaxTree.getChildCount(childNode);
            for (int i = 0; i < childCount; i++) {
                int childNode2 = syntaxTree.getChildNode(childNode, i);
                if (syntaxTree.isBlockNode(childNode2)) {
                    int childNode3 = syntaxTree.getChildNode(childNode2, 0);
                    int childCount2 = syntaxTree.getChildCount(childNode3);
                    for (int i2 = 0; i2 < childCount2; i2++) {
                        int childNode4 = syntaxTree.getChildNode(childNode3, i2);
                        if (203 == syntaxTree.getSyntaxTag(childNode4)) {
                            int childNode5 = syntaxTree.getChildNode(syntaxTree.getChildNode(childNode4, 0), 0);
                            if (syntaxTree.isIdentifierNode(childNode5) && syntaxTree.getIdentifierString(childNode5).isEmpty() && "package".equals(syntaxTree.getIdentifierString(syntaxTree.getChildNode(syntaxTree.getChildNode(childNode4, 0), 2)))) {
                                return syntaxTree.getLiteralString(syntaxTree.getChildNode(syntaxTree.getChildNode(childNode4, 2), 1));
                            }
                        }
                    }
                }
            }
            return "";
        }
        String pathString = parentDirectory.getParentDirectory().getParentDirectory().getPathString();
        Map<String, List<String>> hashMap = new HashMap<>();
        hashMap.put(pathString, null);
        try {
            Iterator<Map.Entry<String, String>> it = AndroidProjectSupport.Z1(hashMap, null).entrySet().iterator();
            return it.hasNext() ? WearAppProjectSupport.gn(it.next().getValue(), null, null) : "";
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    /**
     * Transforms entry name to tag name.
     * <p>
     * For example: `AndroidManifestUsesPermission` -> `uses-permission`
     */
    public static String transformToTagName(String entryName, String prefix) {
        StringBuilder name = new StringBuilder();
        int index = prefix.length();
        while (index < entryName.length()) {
            char c = entryName.charAt(index);
            if (Character.isUpperCase(c)) {
                if (index != prefix.length()) {
                    name.append('-');
                }
                c = Character.toLowerCase(c);
            }
            name.append(c);
            ++index;
        }
        return name.toString();
    }

    /**
     * Transforms tag name to entry name.
     * <p>
     * For example: `uses-permission` -> `AndroidManifestUsesPermission`
     */
    public static String transformToEntryName(String tagName, String prefix) {
        if ("manifest".equals(tagName)) {
            return MANIFEST_TAG_PREFIX;
        }
        StringBuilder name = new StringBuilder(prefix != null ? prefix : "");
        boolean capitalize = false;
        for (int i = 0; i < tagName.length(); i++) {
            char c = tagName.charAt(i);
            if (c == '-') {
                capitalize = true;
            } else {
                if (i == 0 || capitalize) {
                    c = Character.toUpperCase(c);
                    capitalize = false;
                }
                name.append(c);
            }
        }
        return name.toString();
    }

    public static List<ResourceEntry> findEntries(SortedMap<String, SortedMap<Short, ResourceEntry>> entries, Short entryId, Predicate<String> test) {
        List<ResourceEntry> result = new ArrayList<>();
        for (Map.Entry<String, SortedMap<Short, ResourceEntry>> entry : entries.entrySet()) {
            if (test.test(entry.getKey())) {
                ResourceEntry element;
                if (entryId != null) {
                    element = entry.getValue().getOrDefault(entryId, entry.getValue().get(null));
                } else {
                    element = entry.getValue().get(entry.getValue().keySet().iterator().next());
                }
                if (element != null) {
                    result.add(element);
                }
            }
        }
        return result;
    }

    public static boolean hasType(AttributeResource attrEntry, Resources.Attribute.FormatFlags check) {
        return hasType(attrEntry.getTypeMask(), check.getNumber());
    }

    public static boolean hasType(int typeMask, int check) {
        return (typeMask & check) != 0;
    }

    /**
     * 补全tag所需的方法
     */

    private static void codeCompletion(String[] strings, final Model model) {
        Arrays.stream(strings).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Namespace memberNamespace = model.entitySpace.getRootNamespace().getMemberNamespace(model.identifierSpace.get(s));
                model.codeCompleterCallback.aM(memberNamespace, s);
            }
        });
    }

    private static Namespace getSimpleClass(String clazzName, final Model model) {
        final Namespace[] namespace = {model.entitySpace.getRootNamespace()};
        Arrays.stream(clazzName.split("[.$]")).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                namespace[0] = namespace[0].getMemberNamespace(model.identifierSpace.get(s));
            }
        });
        return namespace[0];
    }

    /**
     * findNodeStyleables
     * 查找attr
     */
    private static Set<Styleable> findNodeStyleables(
            String nodeName,
            String parentName,
            ResourceGroup styleables
    ) {
        WidgetTable widgets = widgetTableUtil.getWidgetTable();
        if (widgets == null) {
            return Collections.emptySet();
        }

        // 查找小部件
        Widget widget;
        if (nodeName.contains(".")) {
            widget = widgets.getWidget(nodeName);
        } else {
            widget = widgets.findWidgetWithSimpleName(nodeName);
        }

        if (widget != null) {
            // Android SDK 中的 widget，可获取其父类等信息
            return findStyleablesForWidget(styleables, widgets, widget, parentName, true, "");
        } else if (nodeName.contains(".")) {
            // 可能是自定义视图或库中的视图
            // 只有在 tag 名称符合命名规范时才提供自动完成
            // 此方法仅在 tag 名称已限定时调用
            return findStyleablesForName(styleables, nodeName, parentName, true, "");
        }
        AppLog.d("XmlCompletionUtils", "Cannot find styleable entries for tag: null");
        return Collections.emptySet();
    }

    /**
     * findStyleablesForWidget
     * 添加组件的样式attr
     */
    private static Set<Styleable> findStyleablesForWidget(
            ResourceGroup styleables,
            WidgetTable widgets,
            Widget widget,
            String parentName,
            boolean addFromParent,
            String suffix
    ) {
        Set<Styleable> result = new HashSet<>();

        // 找到 resource group 中的 <declare-styleable> 条目
        addWidgetStyleable(styleables, widget, result, suffix);

        // 查找所有超类的样式
        addSuperclassStyleables(styleables, widgets, widget, result, suffix);

        // 根据布局参数添加属性
        if (addFromParent && parentName != null) {
            Widget parentWidget = parentName.contains(".") ?
                    widgets.getWidget(parentName) :
                    widgets.findWidgetWithSimpleName(parentName);

            if (parentWidget != null) {
                result.addAll(findStyleablesForWidget(
                        styleables, widgets, parentWidget, parentName, false, "_Layout"
                ));
            } else {
                result.addAll(findLayoutParams(styleables, parentName));
            }
        }

        return result;
    }

    private static void addWidgetStyleable(
            ResourceGroup styleables,
            Widget widget,
            Set<Styleable> result,
            String suffix
    ) {
        addWidgetStyleable(styleables, widget.getSimpleName(), result, suffix);
    }

    // 重载方法以提供 `suffix` 的默认值
    private static void addWidgetStyleable(
            ResourceGroup styleables,
            Widget widget,
            Set<Styleable> result
    ) {
        addWidgetStyleable(styleables, widget, result, "");
    }

    private static void addWidgetStyleable(
            ResourceGroup styleables,
            String widget,
            Set<Styleable> result,
            String suffix
    ) {
        Styleable entry = findStyleableEntry(styleables, widget + suffix);
        if (entry != null) {
            result.add(entry);
        }
    }

    // 重载方法以提供 `suffix` 的默认值
    private static void addWidgetStyleable(
            ResourceGroup styleables,
            String widget,
            Set<Styleable> result
    ) {
        addWidgetStyleable(styleables, widget, result, "");
    }

    private static Styleable findStyleableEntry(ResourceGroup styleables, String name) {
        ResourceEntry entry = styleables.findEntry(name, null);
        if (entry == null) {
            AppLog.d("XmlCompletionUtils", "Cannot find styleable for {}", name);
            return null;
        }

        ResourceConfigValue resourceValue = entry.findValue(new ConfigDescription(), "");
        if (resourceValue == null || !(resourceValue.getValue() instanceof Styleable)) {
            AppLog.d("XmlCompletionUtils", "Cannot find styleable for {}", name);
            return null;
        }

        return (Styleable) resourceValue.getValue();
    }

    private static void addSuperclassStyleables(
            ResourceGroup styleables,
            WidgetTable widgets,
            Widget widget,
            Set<Styleable> result,
            String suffix
    ) {
        for (String superclass : widget.getSuperclasses()) {
            // 当遇到 ViewGroup 超类时，添加 margin layout 参数
            if ("android.view.ViewGroup".equals(superclass)) {
                addWidgetStyleable(styleables, "ViewGroup", result, "_MarginLayout");
            }

            Widget superWidget = widgets.getWidget(superclass);
            if (superWidget == null) {
                continue;
            }

            addWidgetStyleable(styleables, superWidget.getSimpleName(), result, suffix);
        }
    }

    private static Set<Styleable> findStyleablesForName(
            ResourceGroup styleables,
            String nodeName,
            String parentName,
            boolean addFromParent,
            String suffix
    ) {
        Set<Styleable> result = new HashSet<>();

        // 样式必须由 View 类的简单名称定义
        String name = nodeName;
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }

        // 为所有视图添加通用属性
        addWidgetStyleable(styleables, "View", result);

        // 查找声明的 styleable
        Styleable entry = findStyleableEntry(styleables, name + suffix);
        if (entry != null) {
            result.add(entry);
        }

        // 如果需要添加来自父级的布局参数，则检查 parentName 并添加相应的参数
        if (addFromParent && parentName != null) {
            result.addAll(findLayoutParams(styleables, parentName));
        }

        return result;
    }

    /// 重载方法以提供默认参数
    private static Set<Styleable> findStyleablesForName(
            ResourceGroup styleables,
            String nodeName,
            String parentName
    ) {
        return findStyleablesForName(styleables, nodeName, parentName, false, "");
    }

    private static Set<Styleable> findLayoutParams(ResourceGroup styleables, String parentName) {
        Set<Styleable> result = new HashSet<>();

        // 为所有视图组和支持子元素边距的视图组添加通用布局参数
        addWidgetStyleable(styleables, "ViewGroup", result, "_Layout");
        addWidgetStyleable(styleables, "ViewGroup", result, "_MarginLayout");

        String name = parentName;
        if (name.contains(".")) {
            name = name.substring(name.lastIndexOf('.') + 1);
        }

        addWidgetStyleable(styleables, name, result, "_Layout");
        return result;
    }


    /**
     * 以下都是attr补全所需的
     */

    // 重载方法以提供 `suffix` 的默认值
    public void addSuperclassStyleables(
            ResourceGroup styleables,
            WidgetTable widgets,
            Widget widget,
            Set<Styleable> result
    ) {
        addSuperclassStyleables(styleables, widgets, widget, result, "");
    }

    public static ResourceUtils getResourceUtil() {
        return resourceUtil;
    }

    public static ApiVersionsUtils getApiVersionsUtil() {
        return apiVersionsUtil;
    }

    public static WidgetTableUtils getWidgetTableUtil() {
        return widgetTableUtil;
    }

    public static JavaViewUtils getJavaViewUtils() {
        return javaViewUtils;
    }



}
