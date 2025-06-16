package io.papermc.generator.resources;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import io.papermc.generator.types.Types;
import io.papermc.generator.utils.SourceCodecs;
import io.papermc.typewriter.ClassNamed;
import java.lang.constant.ConstantDescs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record RegistryData(
    Api api,
    Impl impl,
    Optional<Builder> builder,
    Optional<String> serializationUpdaterField,
    boolean allowInline
) {

    public static final Codec<RegistryData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Api.CODEC.fieldOf("api").forGetter(RegistryData::api),
        Impl.CODEC.fieldOf("impl").forGetter(RegistryData::impl),
        Builder.CODEC.optionalFieldOf("builder").forGetter(RegistryData::builder),
        SourceCodecs.IDENTIFIER.optionalFieldOf("serialization_updater_field").forGetter(RegistryData::serializationUpdaterField),
        Codec.BOOL.optionalFieldOf("allow_inline", false).forGetter(RegistryData::allowInline)
    ).apply(instance, RegistryData::new));

    public record Api(ClassNamed klass, Optional<ClassNamed> holders, Type type, Optional<List<ParentClass>> parentClasses, boolean keyClassNameRelate, Optional<String> registryField) {
        public Api(ClassNamed klass) {
            this(klass, Optional.of(klass), Type.INTERFACE, Optional.empty(), false, Optional.empty());
        }

        public static final Codec<Api> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SourceCodecs.CLASS_NAMED.fieldOf("class").forGetter(Api::klass),
            SourceCodecs.CLASS_NAMED.optionalFieldOf("holders").forGetter(Api::holders),
            Type.CODEC.optionalFieldOf("type", Type.INTERFACE).forGetter(Api::type),
            ExtraCodecs.compactListCodec(ParentClass.CODEC, ExtraCodecs.nonEmptyList(ParentClass.CODEC.listOf())).optionalFieldOf("extends").forGetter(Api::parentClasses),
            Codec.BOOL.optionalFieldOf("key_class_name_relate", false).forGetter(Api::keyClassNameRelate),
            SourceCodecs.IDENTIFIER.optionalFieldOf("registry_field").forGetter(Api::registryField)
        ).apply(instance, Api::new));

        public static final Codec<Api> CLASS_ONLY_CODEC = SourceCodecs.CLASS_NAMED.xmap(Api::new, Api::klass);

        public static final Codec<Api> CODEC = Codec.either(CLASS_ONLY_CODEC, DIRECT_CODEC).xmap(Either::unwrap, api -> {
            if ((api.holders().isEmpty() || api.klass().equals(api.holders().get())) &&
                api.type() == Type.INTERFACE && !api.keyClassNameRelate() && api.registryField().isEmpty()) {
                return Either.left(api);
            }
            return Either.right(api);
        });

        public TypeName getType() {
            ClassName klass = Types.typed(this.klass);
            if (this.parentClasses.isPresent()) {
                List<ParentClass> arguments = this.parentClasses.get();
                TypeName[] args = new TypeName[arguments.size()];
                for (int i = 0; i < arguments.size(); i ++) {
                    args[i] = WildcardTypeName.subtypeOf(arguments.get(i).getType());
                }
                return ParameterizedTypeName.get(klass, args);
            }
            return klass;
        }

        public enum Type implements StringRepresentable {
            INTERFACE("interface"),
            CLASS("class"),
            @Deprecated(since = "1.8")
            ENUM("enum");

            private final String name;
            static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

            Type(String name) {
                this.name = name;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }

        public record ParentClass(ClassNamed klass, Optional<List<ParentClass>> arguments) {
            public ParentClass(ClassNamed value) {
                this(value, Optional.empty());
            }

            public static final Codec<ParentClass> CLASS_ONLY_CODEC = SourceCodecs.CLASS_NAMED.xmap(ParentClass::new, ParentClass::klass);

            private static Codec<ParentClass> directCodec(Codec<ParentClass> codec) {
                return RecordCodecBuilder.create(instance -> instance.group(
                    SourceCodecs.CLASS_NAMED.fieldOf("class").forGetter(ParentClass::klass),
                    ExtraCodecs.compactListCodec(codec, ExtraCodecs.nonEmptyList(codec.listOf())).optionalFieldOf("arguments").forGetter(ParentClass::arguments)
                ).apply(instance, ParentClass::new));
            }

            public static final Codec<ParentClass> CODEC = Codec.recursive("ParentClasses", codec -> {
                return Codec.either(CLASS_ONLY_CODEC, directCodec(codec)).xmap(Either::unwrap, parentClass -> {
                    if (parentClass.arguments().isEmpty()) {
                        return Either.left(parentClass);
                    }
                    return Either.right(parentClass);
                });
            });

            public TypeName getType() {
                ClassName from = Types.typed(this.klass);
                if (this.arguments.isPresent()) {
                    List<ParentClass> args = this.arguments.get();
                    List<TypeName> arguments = new ArrayList<>(args.size());
                    for (ParentClass arg : args) {
                        arguments.add(arg.getType());
                    }

                    return ParameterizedTypeName.get(from, arguments.toArray(TypeName[]::new));
                }

                return from;
            }
        }
    }

    public record Impl(ClassNamed klass, String instanceMethod, boolean delayed) {
        public Impl(ClassNamed klass) {
            this(klass, ConstantDescs.INIT_NAME, false);
        }

        public static final Codec<Impl> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SourceCodecs.CLASS_NAMED.fieldOf("class").forGetter(Impl::klass),
            SourceCodecs.IDENTIFIER.optionalFieldOf("instance_method", ConstantDescs.INIT_NAME).forGetter(Impl::instanceMethod),
            Codec.BOOL.optionalFieldOf("delayed", false).deprecated(21).forGetter(Impl::delayed)
        ).apply(instance, Impl::new));

        public static final Codec<Impl> CLASS_ONLY_CODEC = SourceCodecs.CLASS_NAMED.xmap(Impl::new, Impl::klass);

        public static final Codec<Impl> CODEC = Codec.either(CLASS_ONLY_CODEC, DIRECT_CODEC).xmap(Either::unwrap, impl -> {
            if (impl.instanceMethod().equals(ConstantDescs.INIT_NAME) && !impl.delayed()) {
                return Either.left(impl);
            }
            return Either.right(impl);
        });
    }

    public record Builder(ClassNamed api, ClassNamed impl, RegisterCapability capability) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SourceCodecs.CLASS_NAMED.fieldOf("api").forGetter(Builder::api),
            SourceCodecs.CLASS_NAMED.fieldOf("impl").forGetter(Builder::impl),
            RegisterCapability.CODEC.optionalFieldOf("capability", RegisterCapability.WRITABLE).forGetter(Builder::capability)
        ).apply(instance, Builder::new));

        public enum RegisterCapability implements StringRepresentable {
            NONE("none"),
            ADDABLE("addable"),
            MODIFIABLE("modifiable"),
            WRITABLE("writable");

            private final String name;
            static final Codec<RegisterCapability> CODEC = StringRepresentable.fromEnum(RegisterCapability::values);

            RegisterCapability(String name) {
                this.name = name;
            }

            public boolean canAdd() {
                return this != MODIFIABLE && this != NONE;
            }

            @Override
            public String getSerializedName() {
                return this.name;
            }
        }
    }
}
