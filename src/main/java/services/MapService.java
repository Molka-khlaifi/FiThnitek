package services;

import java.util.HashMap;
import java.util.Map;

public class MapService {

    private static final Map<String, double[]> VILLES = new HashMap<>();

    static {
        VILLES.put("tunis",     new double[]{36.8188, 10.1658});
        VILLES.put("tinis",     new double[]{36.8188, 10.1658});
        VILLES.put("sfax",      new double[]{34.7406, 10.7603});
        VILLES.put("sousse",    new double[]{35.8245, 10.6346});
        VILLES.put("souss",     new double[]{35.8245, 10.6346});
        VILLES.put("nabeul",    new double[]{36.4561, 10.7376});
        VILLES.put("monastir",  new double[]{35.7643, 10.8113});
        VILLES.put("bizerte",   new double[]{37.2746,  9.8739});
        VILLES.put("gabes",     new double[]{33.8815, 10.0982});
        VILLES.put("gafsa",     new double[]{34.4311,  8.7757});
        VILLES.put("kairouan",  new double[]{35.6781, 10.0963});
        VILLES.put("mahdia",    new double[]{35.5047, 11.0622});
        VILLES.put("medenine",  new double[]{33.3549, 10.5055});
        VILLES.put("tataouine", new double[]{32.9211, 10.4517});
        VILLES.put("tozeur",    new double[]{33.9197,  8.1335});
        VILLES.put("kebili",    new double[]{33.7046,  8.9690});
        VILLES.put("beja",      new double[]{36.7256,  9.1817});
        VILLES.put("jendouba",  new double[]{36.5011,  8.7803});
        VILLES.put("siliana",   new double[]{36.0851,  9.3708});
        VILLES.put("zaghouan",  new double[]{36.4029, 10.1429});
        VILLES.put("manouba",   new double[]{36.8100, 10.0986});
        VILLES.put("ariana",    new double[]{36.8625, 10.1956});
        VILLES.put("hammamet",  new double[]{36.4000, 10.6167});
        VILLES.put("djerba",    new double[]{33.8076, 10.8451});
        VILLES.put("zarzis",    new double[]{33.5036, 11.1122});
        VILLES.put("la marsa",  new double[]{36.8781, 10.3247});
        VILLES.put("ben arous", new double[]{36.7533, 10.2281});
    }

    private static final String LEAFLET_CSS =
            "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css";
    private static final String LEAFLET_JS =
            "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";

    // ── Carte trajet ──────────────────────────────────────────────────────────
    public String buildMapHtml(String depart, String destination) {
        double[] cDep  = getCoords(depart);
        double[] cDest = getCoords(destination);
        String dep  = capitalize(depart);
        String dest = capitalize(destination);

        return "<!DOCTYPE html><html><head><meta charset='utf-8'/>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'/>"
                + "<link rel='stylesheet' href='" + LEAFLET_CSS + "'/>"
                + "<script src='" + LEAFLET_JS + "'></script>"
                + "<style>"
                // Reset complet pour JavaFX WebView
                + "*{margin:0;padding:0;box-sizing:border-box;}"
                + "html{width:100%;height:100%;overflow:hidden;}"
                + "body{width:100%;height:100%;overflow:hidden;background:#070b14;}"
                + "#w{display:flex;flex-direction:column;width:100%;height:100%;}"
                // Barre d'info
                + "#bar{"
                + "background:#0d1117;"
                + "border-bottom:1px solid #1e2235;"
                + "padding:0 20px;"
                + "display:flex;align-items:center;gap:14px;"
                + "height:48px;flex-shrink:0;}"
                + "#rt{color:#f1f5f9;font-weight:700;font-size:14px;"
                + "font-family:'Segoe UI',Arial,sans-serif;}"
                + ".pill{display:inline-flex;align-items:center;gap:5px;"
                + "background:#131724;border:1px solid #2a2d3e;border-radius:20px;"
                + "padding:4px 14px;font-family:'Segoe UI',Arial,sans-serif;"
                + "font-size:12px;color:#94a3b8;}"
                + ".vd{color:#a78bfa;font-weight:700;}"
                + ".vk{color:#34d399;font-weight:700;}"
                // Zone carte - CRITICAL: position absolute pour remplir l'espace
                + "#mw{position:relative;flex:1;min-height:0;}"
                + "#map{position:absolute;top:0;left:0;width:100%;height:100%;}"
                // Loading
                + "#ld{position:absolute;top:50%;left:50%;"
                + "transform:translate(-50%,-50%);z-index:9999;"
                + "background:rgba(13,17,23,0.95);border:1px solid #2a2d3e;"
                + "border-radius:12px;padding:14px 24px;"
                + "color:#818cf8;font-family:'Segoe UI',Arial,sans-serif;"
                + "font-size:13px;display:flex;align-items:center;gap:10px;}"
                + ".sp{width:16px;height:16px;border:2px solid #2a2d3e;"
                + "border-top-color:#6366f1;border-radius:50%;"
                + "animation:rot 0.8s linear infinite;}"
                + "@keyframes rot{to{transform:rotate(360deg);}}"
                // Leaflet dark popup
                + ".leaflet-popup-content-wrapper{"
                + "background:#0d1117!important;border:1px solid #2a2d3e!important;"
                + "border-radius:12px!important;"
                + "box-shadow:0 8px 32px rgba(0,0,0,0.8)!important;}"
                + ".leaflet-popup-tip{background:#0d1117!important;}"
                + ".leaflet-popup-content{"
                + "color:#e2e8f0;font-family:'Segoe UI',Arial,sans-serif;"
                + "font-size:13px;margin:10px 14px;}"
                + ".pt{font-weight:700;font-size:14px;margin-bottom:3px;}"
                + ".ps{color:#64748b;font-size:11px;}"
                // Attribution dark
                + ".leaflet-control-attribution{"
                + "background:rgba(13,17,23,0.8)!important;color:#475569!important;}"
                + ".leaflet-control-attribution a{color:#6366f1!important;}"
                + "</style></head><body>"
                + "<div id='w'>"
                // Barre
                + "<div id='bar'>"
                + "<div id='rt'>&#128640; " + dep + " &rarr; " + dest + "</div>"
                + "<div class='pill'>&#9201;&nbsp;Duree&nbsp;"
                + "<span class='vd' id='dur'>chargement...</span></div>"
                + "<div class='pill'>&#128205;&nbsp;Distance&nbsp;"
                + "<span class='vk' id='dist'>chargement...</span></div>"
                + "</div>"
                // Carte
                + "<div id='mw'>"
                + "<div id='ld'><div class='sp'></div>Calcul de l&apos;itineraire...</div>"
                + "<div id='map'></div>"
                + "</div></div>"
                + "<script>"
                // Coordonnees
                + "var dLa=" + cDep[0]  + ",dLo=" + cDep[1]  + ";"
                + "var aLa=" + cDest[0] + ",aLo=" + cDest[1] + ";"
                + "var dN='" + dep  + "',aN='" + dest + "';"
                // Creer la carte
                + "var map=L.map('map',{"
                + "  zoomControl:false,"
                + "  attributionControl:true,"
                + "  preferCanvas:false"
                + "});"
                + "map.setView([(dLa+aLa)/2,(dLo+aLo)/2],8);"
                // Tuiles OpenStreetMap
                + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{"
                + "  attribution:'&copy; OpenStreetMap',"
                + "  maxZoom:19,"
                + "  subdomains:['a','b','c'],"
                + "  detectRetina:false,"
                + "  updateWhenIdle:false,"
                + "  keepBuffer:4"
                + "}).addTo(map);"
                + "L.control.zoom({position:'bottomright'}).addTo(map);"
                // Fonction invalidateSize exposee globalement
                // (appelee aussi par MapController.java via executeScript)
                + "window.fixMap=function(){"
                + "  map.invalidateSize({animate:false,pan:false});"
                + "};"
                // Appels repetes pour les tuiles grises JavaFX
                + "window.fixMap();"
                + "[100,250,500,800,1500,3000].forEach(function(d){"
                + "  setTimeout(window.fixMap,d);"
                + "});"
                // Marqueurs
                + "function mkIcon(color,emoji,name){"
                + "  return L.divIcon({className:'',"
                + "    html:'<div style=\"background:'+color+';color:#fff;"
                + "font-weight:700;font-size:12px;padding:7px 16px;"
                + "border-radius:24px;white-space:nowrap;"
                + "box-shadow:0 4px 16px rgba(0,0,0,0.6);"
                + "border:2px solid rgba(255,255,255,0.2);"
                + "font-family:Segoe UI,Arial,sans-serif;\">"
                + "'+emoji+' '+name+'</div>',"
                + "    iconAnchor:[0,0]});"
                + "}"
                + "L.marker([dLa,dLo],{icon:mkIcon('#10b981','&#128640;',dN)})"
                + ".addTo(map)"
                + ".bindPopup('<div class=\"pt\">&#128640; '+dN+'</div>"
                + "<div class=\"ps\">Point de depart</div>');"
                + "L.marker([aLa,aLo],{icon:mkIcon('#ef4444','&#127937;',aN)})"
                + ".addTo(map)"
                + ".bindPopup('<div class=\"pt\">&#127937; '+aN+'</div>"
                + "<div class=\"ps\">Destination finale</div>');"
                // OSRM routing
                + "var osrm='https://router.project-osrm.org/route/v1/driving/'"
                + "+dLo+','+dLa+';'+aLo+','+aLa"
                + "+'?overview=full&geometries=geojson';"
                + "var xhr=new XMLHttpRequest();"
                + "xhr.open('GET',osrm,true);"
                + "xhr.onreadystatechange=function(){"
                + "  if(xhr.readyState!==4)return;"
                + "  document.getElementById('ld').style.display='none';"
                + "  if(xhr.status===200){"
                + "    var d=JSON.parse(xhr.responseText);"
                + "    if(d.routes&&d.routes.length){"
                + "      var r=d.routes[0];"
                + "      var km=(r.distance/1000).toFixed(1);"
                + "      var sec=r.duration;"
                + "      var h=Math.floor(sec/3600);"
                + "      var m=Math.floor((sec%3600)/60);"
                + "      document.getElementById('dur').textContent="
                + "        h>0?h+'h '+m+'min':m+' min';"
                + "      document.getElementById('dist').textContent=km+' km';"
                + "      var pts=r.geometry.coordinates.map("
                + "        function(c){return[c[1],c[0]];});"
                + "      L.polyline(pts,{color:'#818cf8',weight:14,"
                + "        opacity:0.18}).addTo(map);"
                + "      L.polyline(pts,{color:'#6366f1',weight:5,"
                + "        opacity:1}).addTo(map);"
                + "      map.fitBounds(L.latLngBounds(pts),"
                + "        {padding:[50,50]});"
                + "      setTimeout(window.fixMap,200);"
                + "      setTimeout(window.fixMap,600);"
                + "    }"
                + "  } else {"
                + "    document.getElementById('dur').textContent='hors ligne';"
                + "    document.getElementById('dist').textContent='hors ligne';"
                + "    L.polyline([[dLa,dLo],[aLa,aLo]],"
                + "      {color:'#6366f1',weight:4,dashArray:'10,6'}).addTo(map);"
                + "    map.fitBounds([[dLa,dLo],[aLa,aLo]],{padding:[60,60]});"
                + "  }"
                + "};"
                + "xhr.send();"
                + "</script></body></html>";
    }

    // ── Vue d'ensemble Tunisie ────────────────────────────────────────────────
    public String buildOverviewMapHtml() {
        StringBuilder markers = new StringBuilder();
        VILLES.forEach((nom, coords) -> {
            if (nom.equals("tinis") || nom.equals("souss")) return;
            markers.append(String.format(
                    "L.circleMarker([%f,%f],"
                            + "{radius:7,color:'#6366f1',"
                            + "fillColor:'#818cf8',fillOpacity:0.85,weight:2})"
                            + ".addTo(map)"
                            + ".bindPopup('<b style=\"color:#e2e8f0;font-family:"
                            + "Segoe UI,Arial,sans-serif\">%s</b>');\n",
                    coords[0], coords[1], capitalize(nom)));
        });

        return "<!DOCTYPE html><html><head><meta charset='utf-8'/>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'/>"
                + "<link rel='stylesheet' href='" + LEAFLET_CSS + "'/>"
                + "<script src='" + LEAFLET_JS + "'></script>"
                + "<style>"
                + "*{margin:0;padding:0;box-sizing:border-box;}"
                + "html,body{width:100%;height:100%;overflow:hidden;background:#070b14;}"
                + "#map{width:100%;height:100%;}"
                + ".leaflet-popup-content-wrapper{"
                + "background:#0d1117!important;border:1px solid #2a2d3e!important;"
                + "border-radius:10px!important;"
                + "box-shadow:0 8px 24px rgba(0,0,0,0.7)!important;}"
                + ".leaflet-popup-tip{background:#0d1117!important;}"
                + ".leaflet-popup-content{"
                + "color:#e2e8f0;font-family:'Segoe UI',Arial,sans-serif;}"
                + ".leaflet-control-attribution{"
                + "background:rgba(13,17,23,0.8)!important;color:#475569!important;}"
                + "</style></head><body>"
                + "<div id='map'></div>"
                + "<script>"
                + "var map=L.map('map',{"
                + "  zoomControl:false,attributionControl:true,preferCanvas:false"
                + "}).setView([33.8869,9.5375],6);"
                + "L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{"
                + "  attribution:'&copy; OpenStreetMap',"
                + "  maxZoom:19,subdomains:['a','b','c'],"
                + "  detectRetina:false,updateWhenIdle:false,keepBuffer:4"
                + "}).addTo(map);"
                + "L.control.zoom({position:'bottomright'}).addTo(map);"
                + "window.fixMap=function(){"
                + "  map.invalidateSize({animate:false,pan:false});"
                + "};"
                + "window.fixMap();"
                + "[100,250,500,1000,2000].forEach(function(d){"
                + "  setTimeout(window.fixMap,d);"
                + "});"
                + markers
                + "</script></body></html>";
    }

    // ── Utilitaires ───────────────────────────────────────────────────────────
    private double[] getCoords(String ville) {
        if (ville == null || ville.isBlank()) return new double[]{36.8188, 10.1658};
        double[] c = VILLES.get(ville.toLowerCase().trim());
        return c != null ? c : new double[]{33.8869, 9.5375};
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        StringBuilder sb = new StringBuilder();
        for (String w : s.trim().split("\\s+")) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}