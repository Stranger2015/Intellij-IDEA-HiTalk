package;
class MapInitializerParsingIssue {
    private static var VAR : logtalk.ds.StringMap<String> = [
        "Something" => "Nothing",
        "Other"     => "Else",     // <<-- Extra comma at end.
    ];
}
