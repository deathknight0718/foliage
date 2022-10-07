package org.foliage.zdjxzz.page.device.dto;

import java.io.Serializable;
import java.util.List;

public class GeographicDTO implements Serializable {

    /**
     * current : {"device":{"id":"328c150c-6e2a-3d99-82b2-d5e2078df930","devcode":"14893189421","name":"asss"},"tid":6538215477812920320,"coordinate":{"longitude":121.58643689172118,"latitude":29.407551709586063},"address":"浙江省宁波市宁海县","province":"浙江省"}
     * geographicsByProvince : [{"device":{"id":"328c150c-6e2a-3d99-82b2-d5e2078df930","devcode":"14893189421","name":"asss"},"tid":6538215477812920320,"coordinate":{"longitude":121.58643689172118,"latitude":29.407551709586063},"address":"浙江省宁波市宁海县","province":"浙江省"}]
     */

    private CurrentBean current;
    private List<GeographicsByProvinceBean> geographicsByProvince;

    public CurrentBean getCurrent() {
        return current;
    }

    public void setCurrent(CurrentBean current) {
        this.current = current;
    }

    public List<GeographicsByProvinceBean> getGeographicsByProvince() {
        return geographicsByProvince;
    }

    public void setGeographicsByProvince(List<GeographicsByProvinceBean> geographicsByProvince) {
        this.geographicsByProvince = geographicsByProvince;
    }

    public static class CurrentBean {

        /**
         * device : {"id":"328c150c-6e2a-3d99-82b2-d5e2078df930","devcode":"14893189421","name":"asss"}
         * tid : 6538215477812920320
         * coordinate : {"longitude":121.58643689172118,"latitude":29.407551709586063}
         * address : 浙江省宁波市宁海县
         * province : 浙江省
         */

        private DeviceBean device;
        private long tid;
        private CoordinateBean coordinate;
        private String address;
        private String province;

        public DeviceBean getDevice() {
            return device;
        }

        public void setDevice(DeviceBean device) {
            this.device = device;
        }

        public long getTid() {
            return tid;
        }

        public void setTid(long tid) {
            this.tid = tid;
        }

        public CoordinateBean getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(CoordinateBean coordinate) {
            this.coordinate = coordinate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public static class DeviceBean {

            /**
             * id : 328c150c-6e2a-3d99-82b2-d5e2078df930
             * devcode : 14893189421
             * name : asss
             */

            private String id;
            private String devcode;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDevcode() {
                return devcode;
            }

            public void setDevcode(String devcode) {
                this.devcode = devcode;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

        public static class CoordinateBean {

            /**
             * longitude : 121.58643689172118
             * latitude : 29.407551709586063
             */

            private double longitude;
            private double latitude;

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

        }

    }

    public static class GeographicsByProvinceBean {

        /**
         * device : {"id":"328c150c-6e2a-3d99-82b2-d5e2078df930","devcode":"14893189421","name":"asss"}
         * tid : 6538215477812920320
         * coordinate : {"longitude":121.58643689172118,"latitude":29.407551709586063}
         * address : 浙江省宁波市宁海县
         * province : 浙江省
         */

        private DeviceBeanX device;
        private long tid;
        private CoordinateBeanX coordinate;
        private String address;
        private String province;

        public DeviceBeanX getDevice() {
            return device;
        }

        public void setDevice(DeviceBeanX device) {
            this.device = device;
        }

        public long getTid() {
            return tid;
        }

        public void setTid(long tid) {
            this.tid = tid;
        }

        public CoordinateBeanX getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(CoordinateBeanX coordinate) {
            this.coordinate = coordinate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public static class DeviceBeanX {

            /**
             * id : 328c150c-6e2a-3d99-82b2-d5e2078df930
             * devcode : 14893189421
             * name : asss
             */

            private String id;
            private String devcode;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDevcode() {
                return devcode;
            }

            public void setDevcode(String devcode) {
                this.devcode = devcode;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

        public static class CoordinateBeanX {

            /**
             * longitude : 121.58643689172118
             * latitude : 29.407551709586063
             */

            private double longitude;
            private double latitude;

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

        }

    }

}
