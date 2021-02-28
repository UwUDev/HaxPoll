package me.uwu.haxpoll;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import me.uwu.haxpoll.proxy.Checker;

import java.io.IOException;

public class VoteThread extends Thread {

    public final static String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36";
    public final static BrowserVersion browser =
            new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME)
                    .setUserAgent(userAgent)
                    .build();

    private final HaxPoll manager;
    private final int count;

    private boolean secure;

    public VoteThread(HaxPoll manager, int count, boolean secure){
        this.count = count;
        this.manager = manager;
        this.secure = secure;
    }

    @Override
    public void run() {
        if(this.isInterrupted()) return;

        String ip = (String) null;
        int port = -1;

        if (secure){
            ip = Checker.working.get(count);
            port = Integer.parseInt(ip.split(":")[1]);
            ip = ip.split(":")[0];
        }

        try (final WebClient webClient = new WebClient(browser, false, ip, port)) {

            final HtmlPage page1 = webClient.getPage(manager.url);

            final HtmlRadioButtonInput button = page1.getFirstByXPath("/html/body/main/form/div/div/div[" + manager.boxSlot + "]/input");
            final HtmlButton button2 = page1.getFirstByXPath("/html/body/main/form/footer/button[1]");

            button.click();
            final UnexpectedPage page2 = button2.click();

            if (secure)
                Checker.working.remove(count);

            if(page2.getWebResponse().getStatusCode() == 200 && page2.getWebResponse().getContentAsString().contains("\"success\":\"success\"")) {
                System.out.println("Vote #" + count + " counted !");
                manager.updateStats(false);
            } else {
                System.out.println("Vote #" + count + " failed :/\tStatusCode=" + page2.getWebResponse().getStatusCode() + "\tContent=" + page2.getWebResponse().getContentAsString());
                manager.updateStats(true);
                manager.retry(count);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Vote #" + count + " failed :/");
            manager.updateStats(true);
            manager.retry(count);
        }
    }
}
