package org.opencourse.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Application configuration class for OpenCourse.
 * 
 * @author !EEExp3rt
 */
@Component
@ConfigurationProperties(prefix = "app")
@Validated
@EnableConfigurationProperties(ApplicationConfig.class)
public class ApplicationConfig {

    @Valid
    @NotNull
    private Activity activity = new Activity();

    // Getter and Setter.

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "ApplicationConfigs{" +
                "activity=" + activity +
                '}';
    }
    /**
     * User activity configurations.
     * 
     * @author !EEExp3rt
     */
    public static class Activity {

        @Valid
        @NotNull
        private Resource resource = new Resource();

        @Valid
        @NotNull
        private Interaction interaction = new Interaction();

        // Getter and Setter.

        public Resource getResource() {
            return resource;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }

        public Interaction getInteraction() {
            return interaction;
        }

        public void setInteraction(Interaction interaction) {
            this.interaction = interaction;
        }

        @Override
        public String toString() {
            return "Activity{" +
                    "resource=" + resource +
                    ", interaction=" + interaction +
                    '}';
        }

        /**
         * User activity configurations in resource operations.
         * 
         * @author !EEExp3rt
         */
        public static class Resource {

            private int add = 0;
            private int delete = 0;
            private int like = 0;
            private int unlike = 0;
            private int view = 0;

            // Getter and Setter.

            public int getAdd() {
                return add;
            }

            public void setAdd(int add) {
                this.add = add;
            }

            public int getDelete() {
                return delete;
            }

            public void setDelete(int delete) {
                this.delete = delete;
            }

            public int getLike() {
                return like;
            }

            public void setLike(int like) {
                this.like = like;
            }

            public int getUnlike() {
                return unlike;
            }

            public void setUnlike(int unlike) {
                this.unlike = unlike;
            }

            public int getView() {
                return view;
            }

            public void setView(int view) {
                this.view = view;
            }

            @Override
            public String toString() {
                return "Resource{" +
                        "add=" + add +
                        ", delete=" + delete +
                        ", like=" + like +
                        ", unlike=" + unlike +
                        ", view=" + view +
                        '}';
            }
        }

        /**
         * User activity configurations in interaction operations.
         * 
         * @author !EEExp3rt
         */
        public static class Interaction {

            private int add = 0;
            private int delete = 0;
            private int like = 0;
            private int unlike = 0;

            // Getter and Setter.

            public int getAdd() {
                return add;
            }

            public void setAdd(int add) {
                this.add = add;
            }

            public int getDelete() {
                return delete;
            }

            public void setDelete(int delete) {
                this.delete = delete;
            }

            public int getLike() {
                return like;
            }

            public void setLike(int like) {
                this.like = like;
            }

            public int getUnlike() {
                return unlike;
            }

            public void setUnlike(int unlike) {
                this.unlike = unlike;
            }

            @Override
            public String toString() {
                return "Interaction{" +
                        "add=" + add +
                        ", delete=" + delete +
                        ", like=" + like +
                        ", unlike=" + unlike +
                        '}';
            }
        }
    }
}
