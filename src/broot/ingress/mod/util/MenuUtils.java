package broot.ingress.mod.util;

import broot.ingress.mod.AboutModActivity;
import broot.ingress.mod.ModItemsActivity;
import com.nianticproject.ingress.common.device.DeviceActivity;
import com.nianticproject.ingress.common.intel.IntelActivity;
import com.nianticproject.ingress.common.inventory.ItemsActivity;
import com.nianticproject.ingress.common.playerprofile.MyProfileActivity;
import com.nianticproject.ingress.common.mission.MissionListActivity;
import com.nianticproject.ingress.common.recruit.RecruitActivity;
import com.nianticproject.ingress.common.passcode.PasscodeTabActivity;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {

    public static MenuTabId[] getTabs() {
        List<MenuTabId> tabs = new ArrayList<MenuTabId>();
        if (Config.itemsTab == Config.ItemsTab.AT_START) {
            tabs.add(MenuTabId.MOD_ITEMS);
        }
        if (Config.showOrigItemsTab) {
            tabs.add(MenuTabId.INVENTORY);
        }
        if (Config.showAgentTab) {
            tabs.add(MenuTabId.AGENT);
        }
        if (Config.showMissionTab) {
            tabs.add(MenuTabId.MISSIONS);
        }
        if (Config.showIntelTab) {
            tabs.add(MenuTabId.INTEL);
        }
        if (Config.showRecruitTab) {
            tabs.add(MenuTabId.RECRUIT);
        }
        if (Config.showPasscodeTab) {
            tabs.add(MenuTabId.PASSCODE);
        }
        if (Config.showDeviceTab) {
            tabs.add(MenuTabId.DEVICE);
        }
        if (Config.itemsTab == Config.ItemsTab.AT_END) {
            tabs.add(MenuTabId.MOD_ITEMS);
        }
        tabs.add(MenuTabId.MOD_ABOUT);
        return tabs.toArray(new MenuTabId[tabs.size()]);
    }

    public static Class<?> getActivityClassForMenuTabId(MenuTabId tab) {
        switch (tab) {
            case MOD_ITEMS:
                return ModItemsActivity.class;
            case INVENTORY:
                return ItemsActivity.class;
            case AGENT:
                return MyProfileActivity.class;
            case MISSIONS:
                return MissionListActivity.class;
            case INTEL:
                return IntelActivity.class;
            case RECRUIT:
                return RecruitActivity.class;
            case PASSCODE:
                return PasscodeTabActivity.class;
            case DEVICE:
                return DeviceActivity.class;
            case MOD_ABOUT:
                return AboutModActivity.class;
            default:
                throw new RuntimeException();
        }
    }
}
