package com.example.chillStream.service;

import com.example.chillStream.dto.AdDto;
import com.example.chillStream.dto.VideoFormDto;
import com.example.chillStream.entity.*;
import com.example.chillStream.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import java.io.File;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class ContentsService {
    @Value("${videoLocation}") // application.properties에 있는 videoLocation을 말함
    private String videoLocation;

    @Value("${thumbnailLocation}")
    private String thumbnailLocation;

    @Value("${previewLocation}")
    private String previewLocation;

    @Value("${subtitleLocation}")
    private String subtitleLocation;

    @Value("${adLocation}")
    private String adLocation;

    @Value("${bannerLocation}")
    private String bannerLocation;

    private final ThumbnailRepository thumbnailRepository;
    private final VideoRepository videoRepository;
    private final PreviewRepository previewRepository;
    private final AdRepository adRepository;
    private final FileService fileService;
    private final SubtitleRepository subtitleRepository;
    private final BannerImgRepository bannerImgRepository;


    public void saveThumbnailFile(Thumbnail thumbnail, MultipartFile thumbnailFile) throws Exception{
        String oriThumbnailName = thumbnailFile.getOriginalFilename(); // 본래 오리지널 썸네일 경로
        String thumbnailName = "";
        String thumbnailUrl = "";
        if (!StringUtils.isEmpty(oriThumbnailName)){
            thumbnailName = fileService.uploadFile(thumbnailLocation, oriThumbnailName,thumbnailFile.getBytes());
            thumbnailUrl = "/thumbnail/"+thumbnailName;
        }
        thumbnail.updateThumbnail(oriThumbnailName,thumbnailName,thumbnailUrl);
        thumbnailRepository.save(thumbnail);
    }

    public void updateThumbnail(Long thumbnailId,MultipartFile thumbnailFile) throws Exception {
        if(!thumbnailFile.isEmpty()){
            Thumbnail savedThumbnailFile = thumbnailRepository.findById(thumbnailId).orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isEmpty(savedThumbnailFile.getThumbnailName())){
                fileService.deleteFile(thumbnailLocation+"/"+savedThumbnailFile.getThumbnailName());
            }
            String oriThumbnailName = thumbnailFile.getOriginalFilename();
            String thumbnailName = fileService.uploadFile(thumbnailLocation,oriThumbnailName,thumbnailFile.getBytes());
            String thumbnailUrl = "/thumbnail/"+thumbnailName;
            savedThumbnailFile.updateThumbnail(oriThumbnailName,thumbnailName,thumbnailUrl);
        }
    }

    public void savePreviewFile(Preview preview, MultipartFile previewFile) throws Exception{
        String oriPreviewName = previewFile.getOriginalFilename(); // 본래 오리지널 미리보기 경로
        String previewName = "";
        String previewUrl = "";
        if (!StringUtils.isEmpty(oriPreviewName)){
            previewName = fileService.uploadFile(previewLocation, oriPreviewName,previewFile.getBytes());
            previewUrl = "/preview/"+previewName;
        }
        preview.updatePreview(oriPreviewName,previewName,previewUrl);
        previewRepository.save(preview);
    }

    public void updatePreview(Long previewId,MultipartFile previewFile) throws Exception {
        if(!previewFile.isEmpty()){
            Preview savedPreviewFile = previewRepository.findById(previewId).orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isEmpty(savedPreviewFile.getPreviewName())){
                fileService.deleteFile(previewLocation+"/"+savedPreviewFile.getPreviewName());
            }
            String oriPreviewName = previewFile.getOriginalFilename();
            String previewName = fileService.uploadFile(previewLocation,oriPreviewName,previewFile.getBytes());
            String previewUrl = "/preview/"+previewName;
            savedPreviewFile.updatePreview(oriPreviewName,previewName,previewUrl);
        }
    }
    
    public void saveVideoFile(VideoFormDto videoFormDto, Video video, MultipartFile videoFile) throws Exception{
        String oriVideoName = videoFile.getOriginalFilename(); // 본래 오리지널 동영상 경로
        String videoName = "";
        String videoUrl = "";
        String title = videoFormDto.getTitle();
        Integer episodeNumber;
        if (!StringUtils.isEmpty(oriVideoName)){
            videoName = fileService.uploadFile(videoLocation, oriVideoName,videoFile.getBytes());
            videoUrl = "/video1/"+videoName;
        }
        if(videoFormDto.getEpisodeNumber() == null){
            episodeNumber = 0;
        }
        else{
            episodeNumber = videoFormDto.getEpisodeNumber();
        }
        video.updateVideo(oriVideoName,videoName,videoUrl,title,episodeNumber);
        videoRepository.save(video);
    }
    
    public void updateVideo(VideoFormDto videoFormDto,MultipartFile videoFile) throws Exception {
        Long videoId = videoFormDto.getId();
        String title = videoFormDto.getTitle();
        Integer episodeNumber = videoFormDto.getEpisodeNumber();
        Video savedVideoFile = videoRepository.findById(videoId).orElseThrow(EntityNotFoundException::new);
        if(!videoFile.isEmpty()){ //새로운 파일이 있으면
            if(!StringUtils.isEmpty(savedVideoFile.getVideoName())){
                fileService.deleteFile(videoLocation+"/"+savedVideoFile.getVideoName());
            }
            String oriVideoName = videoFile.getOriginalFilename();
            String videoName = fileService.uploadFile(videoLocation,oriVideoName,videoFile.getBytes());
            String videoUrl = "/video1/"+videoName;
            savedVideoFile.updateVideo(oriVideoName,videoName,videoUrl,title,episodeNumber);
        }
        else{//새로운 파일이 없으면
            savedVideoFile.updateTitleAndNumber(title,episodeNumber);
        }
    }

    public void saveSubtitleFile(Subtitle subtitle, MultipartFile subtitleFile) throws Exception {
        String oriSubtitleName = subtitleFile.getOriginalFilename();
        String subtitleName = "";
        String subtitleUrl = "";
        if(!StringUtils.isEmpty(oriSubtitleName)){
            subtitleName = fileService.uploadFile(subtitleLocation, oriSubtitleName, subtitleFile.getBytes());
            subtitleUrl = "/subtitle/"+subtitleName;
        }
        subtitle.updateSubtitle(oriSubtitleName, subtitleName, subtitleUrl);
        subtitleRepository.save(subtitle);
    }

    public void updateSubtitle(Long subtitleId, MultipartFile subtitleFile) throws Exception {
        if(!subtitleFile.isEmpty()){
            Subtitle savedSubtitleFile = subtitleRepository.findById(subtitleId).orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isEmpty(savedSubtitleFile.getSubtitleName())){
                fileService.deleteFile(subtitleLocation+"/"+savedSubtitleFile.getSubtitleName());
            }
            String oriSubtitleName = subtitleFile.getOriginalFilename();
            String subtitleName = fileService.uploadFile(subtitleLocation,oriSubtitleName, subtitleFile.getBytes());
            String subtitleUrl = "/subtitle/"+subtitleName;
            savedSubtitleFile.updateSubtitle(oriSubtitleName,subtitleName,subtitleUrl);
        }
    }

    public void saveAdFile(Ad ad, MultipartFile adFile) throws Exception{
        String oriAdName = adFile.getOriginalFilename(); // 본래 오리지널 썸네일 경로
        String adName = "";
        String adUrl = "";
        System.out.println(oriAdName);
        if (!StringUtils.isEmpty(oriAdName)){
            adName = fileService.uploadFile(adLocation, oriAdName,adFile.getBytes());
            adUrl = "/ad/"+adName;
        }
        ad.updateAd(ad.getTitle(), oriAdName,adName,adUrl,ad.getAdClickUrl());
        adRepository.save(ad);
    }
    
    public void updateAd(AdDto adDto, MultipartFile adFile) throws Exception {
        Ad savedAdFile = adRepository.findById(adDto.getId()).orElseThrow(EntityNotFoundException::new); //저장된 파일 조회
        if (!adFile.isEmpty()) { //새로운 파일이 있으면서
            if (!StringUtils.isEmpty(savedAdFile.getAdName())) { // 저장된 파일이 존재하면
                fileService.deleteFile(adLocation + "/" + savedAdFile.getAdName()); //해당 파일 삭제
            }
            String oriAdName = adFile.getOriginalFilename();
            String adName = fileService.uploadFile(adLocation, oriAdName, adFile.getBytes()); //새 파일로 업로드
            String adUrl = "/ad/" + adName;
            savedAdFile.updateAd(adDto.getTitle(), oriAdName, adName, adUrl, adDto.getAdClickUrl()); //전체 업데이트
        } else { //새로운 파일이 없으면
            savedAdFile.updateTitleUrl(adDto.getTitle(), adDto.getAdClickUrl()); //제목과 클릭url만 업데이트
        }
    }

    

    // 배너 관련 메서드

    public void saveBannerFile(BannerImg bannerImg, MultipartFile bannerImgFile) throws Exception {
        String oriImgName = bannerImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if(!StringUtils.isEmpty(oriImgName)) {
            imgName = UUID.randomUUID().toString() + "_" + oriImgName;
            String uploadPath = bannerLocation + "/" + imgName;
            FileUtils.writeByteArrayToFile(new File(uploadPath), bannerImgFile.getBytes());
            imgUrl = "/banner/" + imgName;
        }

        bannerImg.updateBannerImg(oriImgName, imgName, imgUrl);
    }

    

    public void updateBannerFile(Long bannerId, MultipartFile bannerImgFile) throws Exception {
        if(!bannerImgFile.isEmpty()) {
            BannerImg savedBannerImgFile = bannerImgRepository.findById(bannerId)
                .orElseThrow(EntityNotFoundException::new);
            
            if(!StringUtils.isEmpty(savedBannerImgFile.getImgName())) {
                fileService.deleteFile(bannerLocation + "/" + savedBannerImgFile.getImgName());
            }
            
            String oriBannerImgName = bannerImgFile.getOriginalFilename();
            String bannerImgName = fileService.uploadFile(bannerLocation, oriBannerImgName, bannerImgFile.getBytes());
            String bannerImgUrl = "/banner/" + bannerImgName;
            
            savedBannerImgFile.updateBannerImg(oriBannerImgName, bannerImgName, bannerImgUrl);
        }
    }



}
