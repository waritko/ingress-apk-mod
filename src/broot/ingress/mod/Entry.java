package broot.ingress.mod;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import broot.ingress.mod.util.Config;
import broot.ingress.mod.util.InventoryUtils;
import broot.ingress.mod.util.MenuUtils;
import broot.ingress.mod.util.UiVariant;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;
import com.nianticproject.ingress.NemesisActivity;
import com.nianticproject.ingress.common.app.NemesisMemoryCache;
import com.nianticproject.ingress.common.app.NemesisMemoryCacheFactory;
import com.nianticproject.ingress.common.app.NemesisWorld;
import com.nianticproject.ingress.common.assets.AssetFinder;
import com.nianticproject.ingress.common.inventory.MenuControllerImpl;
import com.nianticproject.ingress.common.ui.BaseSubActivity;
import com.nianticproject.ingress.common.ui.FormatUtils;
import com.nianticproject.ingress.common.ui.elements.PortalInfoDialog;
import com.nianticproject.ingress.common.ui.widget.MenuTabId;
import com.nianticproject.ingress.common.upgrade.PortalUpgradeUi;
import com.nianticproject.ingress.gameentity.components.LocationE6;
import com.nianticproject.ingress.shared.ClientType;
import com.nianticproject.ingress.shared.location.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Entry {

    private static Label portalInfoDistLabel;
    
    private static SimpleDateFormat tf12 = new SimpleDateFormat("h:mma");
    private static SimpleDateFormat tf24 = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat tf24ns = new SimpleDateFormat("HH:mm");

    static {
        Mod.init();
    }

    public static void NemesisApp_onOnCreate(Application app) {
        Mod.app = app;
        Config.load();
        Mod.displayMetrics = new DisplayMetrics();
        ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(Mod.displayMetrics);
    }

    public static void CollectItemsFromPortalCallBack_callback(com.nianticproject.ingress.common.callback.CollectItemsFromPortalCallBack cb, com.nianticproject.ingress.shared.rpc.RpcResult res) 
    {
        com.nianticproject.ingress.gameentity.components.Portal portal = cb.portal;
        try 
        {
            java.util.HashMap hashmap = new java.util.HashMap();
            hashmap.put("portal", portal);
            hashmap.put("portalTeam", portal.getEntity().getComponent(com.nianticproject.ingress.gameentity.components.ControllingTeam.class));
            hashmap.put("rpcResult", res);
            java.lang.String s = com.nianticproject.ingress.common.json.JacksonInitializer.objectMapper.writeValueAsString(hashmap);
            //android.util.Log.e("sdiz", s);
            byte abyte0[] = s.getBytes();
            java.io.ByteArrayOutputStream bytearrayoutputstream = new java.io.ByteArrayOutputStream();
            java.util.zip.GZIPOutputStream gzipoutputstream = new java.util.zip.GZIPOutputStream(bytearrayoutputstream);
            gzipoutputstream.write(abyte0);
            gzipoutputstream.close();
            byte abyte1[] = bytearrayoutputstream.toByteArray();
            java.net.HttpURLConnection httpurlconnection = (java.net.HttpURLConnection)(new java.net.URL("http://broot.varak.net/hacklog.php")).openConnection();
            httpurlconnection.setRequestProperty("Content-Encoding", "gzip");
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setDoOutput(true);
            java.io.BufferedOutputStream bufferedoutputstream = new java.io.BufferedOutputStream(httpurlconnection.getOutputStream());
            bufferedoutputstream.write(abyte1);
            bufferedoutputstream.flush();
            bufferedoutputstream.close();
            httpurlconnection.getResponseCode();
            httpurlconnection.disconnect();
            return;
        }
        catch(java.lang.Throwable obj)
        {
            android.util.Log.e("waritko", "err", ((java.lang.Throwable) (obj)));
        }
    }

    public static void NemesisActivity_onOnCreate(NemesisActivity activity) {
        PowerManager pm;
        Mod.nemesisActivity = activity;
        Mod.updateFullscreenMode();
        pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        Mod.ksoWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Ingress - Keep Screen ON");
        Mod.updateKeepScreenOn();
    }

    public static void NemesisActivity_onOnPause(NemesisActivity activity) {
        if (Config.keepScreenOn) {
            if (Mod.ksoWakeLock.isHeld()) {
                Mod.ksoWakeLock.release();
            }
        }
    }

    public static void NemesisActivity_onOnResume(NemesisActivity activity) {
        if (Config.keepScreenOn) {
            if (!Mod.ksoWakeLock.isHeld()) {
                Mod.ksoWakeLock.acquire();
            }
        }
    }

    public static void NemesisWorld_onInit(NemesisWorld world) {
        Mod.world = world;
    }

    public static void SubActivityManager_onInit(List<BaseSubActivity> activities) {
        activities.add(new AboutModActivity());
        activities.add(new ModItemsActivity());
    }

    public static void MenuController_onInit(MenuControllerImpl menuController) {
        Mod.menuController = menuController;
    }

    public static void AssetFinder_onInit(AssetFinder assetFinder) {
        Mod.assetFinder = assetFinder;
        Mod.updateCurrUiVariant();
    }

    // At this point most stuff should be already initialized
    public static void SubActiityApplicationLisener_onCreated() {
        Mod.cache = (NemesisMemoryCache) NemesisMemoryCacheFactory.getCache();
        Mod.skin = Mod.world.getSubActivityManager().skin;
    }

    public static Class<?> MenuShowBtn_onClick() {
        return MenuUtils.getActivityClassForMenuTabId(MenuUtils.getTabs()[0]);
    }

    public static MenuTabId[] MenuTopWidget_getTabs() {
        return MenuUtils.getTabs();
    }

    public static String MenuTabId_onToString(MenuTabId tab) {
        switch (tab) {
            case MOD_ABOUT:
                return "[MOD]";
            case MOD_ITEMS:
                return "[ITEMS]";
        }
        return null;
    }

    public static void MenuControllerImpl_onSelectTab(MenuTabId tabId) {
        Mod.world.getSubActivityManager().replaceForegroundActivity(MenuUtils.getActivityClassForMenuTabId(tabId));
    }

    public static FileHandle AssetFinder_onGetAssetPath(String in) {
        if (!in.startsWith("{data:")) {
            return null;
        }
        int pos1 = in.indexOf("/data/", 6);
        int pos2 = in.indexOf(",", pos1 + 6);
        String pre = in.substring(6, pos1) + "/";
        String post = "/" + in.substring(pos1 + 6, pos2);

        UiVariant variant = Mod.currUiVariant;
        while (variant != null) {
            FileHandle file = Gdx.files.internal(pre + variant.name + post);
            if (file.exists()) {
                return file;
            }
            variant = UiVariant.byName.get(variant.parent);
        }
        return null;
    }

    public static void PortalInfoDialog_onStatsTableCreated(PortalInfoDialog dialog, Table t) {
        Mod.portalInfoDialog = dialog;

        if (!Config.changePortalInfoDialog) {
            return;
        }

        Label.LabelStyle style = Mod.skin.get("portal-stats", Label.LabelStyle.class);
        Label.LabelStyle keyExistsStyle = Mod.skin.get("ops-title", Label.LabelStyle.class);

        List<Cell> cells = new ArrayList<Cell>(t.getCells());
        t.clear();
        t.add((Actor) cells.get(0).getWidget()).left();
        t.add((Actor) cells.get(1).getWidget()).left().expandX();
        t.row();
        t.add((Actor) cells.get(3).getWidget()).left();
        t.add((Actor) cells.get(4).getWidget()).left().expandX();
        t.row();
        int keys = InventoryUtils.getNumberOfPortalKeys(dialog.portalComponent);
        t.add(new Label("Keys:", keys > 0 ? keyExistsStyle : style)).left();
        t.add(new Label(String.valueOf(keys), keys > 0 ? keyExistsStyle : style)).left().expandX();
        t.row();
        t.add(new Label("Dist.:", style)).left();
        t.add(portalInfoDistLabel = new Label("", style)).left().expandX();
    }

    public static void PortalInfoDialog_onPlayerLocationChanged() {
        if (!Config.changePortalInfoDialog) {
            return;
        }
        double dist = LocationUtils.calculateDistance(
                Mod.world.getPlayerModel().getPlayerLocation().getLatLng(),
                ((LocationE6) Mod.portalInfoDialog.portalComponent.getEntity().getComponent(LocationE6.class)).getLatLng());
        portalInfoDistLabel.setText(FormatUtils.formatDistance((float) dist));
    }

    public static long GpsSensor_lockTimeout() {
        return Config.gpsLockTime;
    }

    public static boolean ScannerTouchHandler_shouldSwapTouchMenuButtons() {
        return Config.swapTouchMenuButtons;
    }

    public static boolean ScannerStateManager_onEnablePortalVectors() {
        return Config.showPortalVectors;
    }

    public static boolean ZoomInMode_shouldZoomIn() {
        return Config.scannerZoomInAnimEnabled;
    }

    public static float PortalInfoDialog_getOpenDelay(float orig) {
        return Config.scannerZoomInAnimEnabled ? orig : 0;
    }

    public static boolean HackController_shouldShowAnimation() {
        return Config.hackAnimEnabled;
    }
    
    public static float HackAnimationStage_getTotalTime(float orig) {
        return Config.hackAnimEnabled ? orig : 0;
    }

    public static boolean InventoryItemRenderer_shouldRotate() {
        return Config.rotateInventoryItemsEnabled;
    }

    public static boolean InventoryItemRenderer_simplifyItems() {
        return Config.simplifyInventoryItems;
    }

    public static ShaderProgram ShaderUtils_compileShader(String vertex, String frag, String name) {
        return new ShaderProgram(vertex, frag);
    }
    
    public static float XmParticleRender_getTimeSec(float orig) {
        return Config.xmFlowEnabled ? orig : 0;
    }
    
    public static float ShieldShader_getRampTargetInvWidthX(float orig) {
    	return Config.shieldAnimEnabled ? orig : 0;
    }
    
    public static float ShieldShader_getRampTargetInvWidthY(float orig) {
    	return Config.shieldAnimEnabled ? orig : 1;
    }

    public static boolean ClientFeatureKnobBundle_getEnableCommsAlertsTab(boolean orig) {
        return Config.commAlertsTab || orig;
    }

    public static ClientType getClientType() {
        return ClientType.DEVELOPMENT;
    }

    public static ClientType getClientTypeForJackson() {
        return ClientType.PRODUCTION;
    }

    public static boolean shouldSkipIntro() {
        return Config.skipIntro;
    }

    public static boolean shouldDrawScannerObject() {
        return Config.scannerObjectsEnabled;
    }

    public static boolean ItemActionHandler_recycleAnimationsEnabled() {
        return Config.recycleAnimationsEnabled;
    }

    public static boolean vibrationEnabled() {
        return Config.vibration;
    }

    public static SimpleDateFormat CommsAdapter_getDateFormat() {
        switch (Config.chatTimeFormat) {
            case 0:  return tf12;
            case 1:  return tf24;
            default:  return tf24ns;
        }
    }
}
