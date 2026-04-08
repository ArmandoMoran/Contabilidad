# Agent Notes

### Telegram update protocol
- User wants Telegram updates during work by default.
- Send at least 3 messages for substantial tasks:
  1. `Started`: before edits or long-running work begin.
  2. `Milestone`: after a meaningful checkpoint such as tests passing, deploy finishing, or push completing.
  3. `Done`: final outcome with branch, commit, PR link, and success/failure status.
- Use existing environment variables:
  - `TELEGRAM_BOT_TOKEN`
  - `TELEGRAM_CHAT_ID`
- Do not print or echo the bot token.
- Reusable PowerShell helper:

```powershell
$token=[Environment]::GetEnvironmentVariable('TELEGRAM_BOT_TOKEN','Process')
if([string]::IsNullOrWhiteSpace($token)){$token=[Environment]::GetEnvironmentVariable('TELEGRAM_BOT_TOKEN','User')}
$chat=[Environment]::GetEnvironmentVariable('TELEGRAM_CHAT_ID','Process')
if([string]::IsNullOrWhiteSpace($chat)){$chat=[Environment]::GetEnvironmentVariable('TELEGRAM_CHAT_ID','User')}
$base="https://api.telegram.org/bot$token"
function Send-Tg([string]$msg){ Invoke-RestMethod -Method Post -Uri "$base/sendMessage" -Body @{ chat_id=$chat; text=$msg } | Out-Null }
```

- Standard message pattern:
  - `Send-Tg "Started: <task summary>"`
  - `Send-Tg "Milestone: <what completed>"`
  - `Send-Tg "Done: <result>; branch=<branch>; commit=<sha>; pr=<url>"`
