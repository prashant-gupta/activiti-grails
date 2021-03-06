import org.activiti.engine.IdentityService
import org.activiti.engine.identity.Group
import org.activiti.engine.identity.Picture
import org.activiti.engine.identity.User
import org.activiti.engine.impl.util.IoUtil
import org.springframework.beans.factory.annotation.Autowired

class BootStrap {

    def identityService;

    def init = { servletContext ->
//        initDemoGroups()
//        initDemoUsers()
        User user = identityService.newUser("kermit");
        user.setFirstName("kermit");
        user.setPassword("kermit");
        user.setEmail("kermit@activiti.org");
        identityService.saveUser(user);
    }
    def destroy = {
    }

     void initDemoGroups() {
        List<String> assignmentGroups = ["management", "sales", "marketing", "engineering"];
        for (String groupId : assignmentGroups) {
            createGroup(groupId, "assignment");
        }

        List<String> securityGroups = ["user", "admin"];
        for (String groupId : securityGroups) {
            createGroup(groupId, "security-role");
        }
    }

     void createGroup(String groupId, String type) {
        if (identityService.createGroupQuery().groupId(groupId).count() == 0) {
            Group newGroup = identityService.newGroup(groupId);
            newGroup.setName(groupId.substring(0, 1).toUpperCase() + groupId.substring(1));
            newGroup.setType(type);
            identityService.saveGroup(newGroup);
        }
    }

     void initDemoUsers() {
        createUser("kermit", "Kermit", "The Frog", "kermit", "kermit@activiti.org",
                "org/activiti/explorer/images/kermit.jpg",
                Arrays.asList("management", "sales", "marketing", "engineering", "user", "admin"),
                Arrays.asList("birthDate", "10-10-1955", "jobTitle", "Muppet", "location", "Hollywoord",
                        "phone", "+123456789", "twitterName", "alfresco", "skype", "activiti_kermit_frog"));

        createUser("gonzo", "Gonzo", "The Great", "gonzo", "gonzo@activiti.org",
                "org/activiti/explorer/images/gonzo.jpg",
                Arrays.asList("management", "sales", "marketing", "user"),
                null);
        createUser("fozzie", "Fozzie", "Bear", "fozzie", "fozzie@activiti.org",
                "org/activiti/explorer/images/fozzie.jpg",
                Arrays.asList("marketing", "engineering", "user"),
                null);
    }

     void createUser(String userId, String firstName, String lastName, String password,
                              String email, String imageResource, List<String> groups, List<String> userInfo) {

        if (identityService.createUserQuery().userId(userId).count() == 0) {

            // Following data can already be set by demo setup script

            User user = identityService.newUser(userId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(password);
            user.setEmail(email);
            identityService.saveUser(user);

            if (groups != null) {
                for (String group : groups) {
                    identityService.createMembership(userId, group);
                }
            }
        }

        // Following data is not set by demo setup script

        // image
        if (imageResource != null) {
            byte[] pictureBytes = IoUtil.readInputStream(this.getClass().getClassLoader().getResourceAsStream(imageResource), null);
            Picture picture = new Picture(pictureBytes, "image/jpeg");
            identityService.setUserPicture(userId, picture);
        }

        // user info
        if (userInfo != null) {
            for(int i=0; i<userInfo.size(); i+=2) {
                identityService.setUserInfo(userId, userInfo.get(i), userInfo.get(i+1));
            }
        }

    }
}
