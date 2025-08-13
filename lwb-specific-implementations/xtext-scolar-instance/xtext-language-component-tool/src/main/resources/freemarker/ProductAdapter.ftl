package ${sourcePackage};

import ${sourcePackage}.*;
import ${targetPackage}.*;

/**
 * Adapter class between product interface of the extension point ${source.name} and
 * the product interface of provision point ${target.name}. This adapter has to be extended by a
 * class with name ${source.name}2${target.name}. Otherwise it cannot be automatically
 * registered by the composed generator.
 */
public abstract class ${source.name}2${target.name}TOP implements ${target.name} {

    protected ${source.name} adaptee;

    public ${source.name}2${target.name}TOP(${source.name} adaptee) {
        this.adaptee = adaptee;
    }

}