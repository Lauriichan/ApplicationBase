package me.lauriichan.applicationbase.app.config.startup;

import java.net.MalformedURLException;

import me.lauriichan.applicationbase.app.config.Configuration;

public interface IPropertyIO<T> {

    final IPropertyIO<String> STRING = new IPropertyIO<>() {
        @Override
        public String read(Configuration configuration, String path) {
            return configuration.get(path, String.class);
        }

        @Override
        public void write(Configuration configuration, String path, String value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Boolean> BOOLEAN = new IPropertyIO<>() {
        @Override
        public Boolean read(Configuration configuration, String path) {
            return configuration.get(path, Boolean.class);
        }

        @Override
        public void write(Configuration configuration, String path, Boolean value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Byte> BYTE = new IPropertyIO<>() {
        @Override
        public Byte read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.byteValue();
        }

        @Override
        public void write(Configuration configuration, String path, Byte value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Short> SHORT = new IPropertyIO<>() {
        @Override
        public Short read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.shortValue();
        }

        @Override
        public void write(Configuration configuration, String path, Short value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Integer> INTEGER = new IPropertyIO<>() {
        @Override
        public Integer read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.intValue();
        }

        @Override
        public void write(Configuration configuration, String path, Integer value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Long> LONG = new IPropertyIO<>() {
        @Override
        public Long read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.longValue();
        }

        @Override
        public void write(Configuration configuration, String path, Long value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Float> FLOAT = new IPropertyIO<>() {
        @Override
        public Float read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.floatValue();
        }

        @Override
        public void write(Configuration configuration, String path, Float value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Double> DOUBLE = new IPropertyIO<>() {
        @Override
        public Double read(Configuration configuration, String path) {
            Number number = configuration.getNumber(path, null);
            if (number == null) {
                return null;
            }
            return number.doubleValue();
        }

        @Override
        public void write(Configuration configuration, String path, Double value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<Number> NUMBER = new IPropertyIO<>() {
        @Override
        public Number read(Configuration configuration, String path) {
            return configuration.getNumber(path, null);
        }

        @Override
        public void write(Configuration configuration, String path, Number value) {
            configuration.set(path, value);
        }
    };
    final IPropertyIO<java.net.URL> URL = new IPropertyIO<>() {
        @Override
        public java.net.URL read(Configuration configuration, String path) {
            String string = configuration.get(path, String.class);
            if (string == null) {
                return null;
            }
            try {
                return new java.net.URL(string);
            } catch (MalformedURLException e) {
                return null;
            }
        }

        @Override
        public void write(Configuration configuration, String path, java.net.URL value) {
            configuration.set(path, value.toString());
        }
    };

    public static <E extends Enum<E>> IPropertyIO<E> ofEnum(Class<E> type) {
        return EnumPropertyIO.of(type);
    }
    
    /*
     * Implementation
     */

    T read(Configuration configuration, String path);

    void write(Configuration configuration, String path, T value);

}
